package com.clunkymachines.lwm2m.manager.repository;

import java.sql.*;
import java.util.*;

import org.eclipse.leshan.core.util.Hex;
import org.eclipse.leshan.servers.security.SecurityInfo;
import org.sqlite.*;

import com.clunkymachines.lwm2m.manager.model.Device;

public class DeviceRepository {

    private final DatabaseManager dbManager;

    // field index in the table
    public enum Field {
        LWM2M_ENDPOINT(1),
        NAME(2),
        DTLS_PSK_ID(3),
        DTLS_PSK(4),
        DTLS_RPK(5),
        DTLS_X509(6);

        public final int field;

        Field(int field) {
            this.field = field;
        }
    }
    public DeviceRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public Device get(String endpoint) {
        String query = "SELECT lwm2m_endpoint, name, dtls_psk_id, dtls_psk, dtls_rpk, dtls_x509 FROM device WHERE lwm2m_endpoint = ?";
        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, endpoint);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDevice(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public Device getByPskIdentity(String pskIdentity) {
        String query = "SELECT lwm2m_endpoint, name, dtls_psk_id, dtls_psk, dtls_rpk, dtls_x509 FROM device WHERE dtls_psk_id = ?";
        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, pskIdentity);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDevice(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<Device> list() {
        String query = "SELECT lwm2m_endpoint, name, dtls_psk_id, dtls_psk, dtls_rpk, dtls_x509 FROM device";
        List<Device> devices = new ArrayList<>();
        try {
            Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery();
             while (rs.next()) {
                devices.add(mapResultSetToDevice(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return devices;
    }

    public void add(Device device) throws UniqueConstraintViolationException {
        String query = "INSERT INTO device (lwm2m_endpoint, name, dtls_psk_id, dtls_psk, dtls_rpk, dtls_x509) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(Field.LWM2M_ENDPOINT.field, device.endpoint());
            stmt.setString(Field.NAME.field, device.name());
            stmt.setString(Field.DTLS_PSK_ID.field, device.securityInfo().getPskIdentity());
            stmt.setString(Field.DTLS_PSK.field, Hex.encodeHexString(device.securityInfo().getPreSharedKey()));
            if (device.securityInfo().getRawPublicKey()!= null) {
                stmt.setString(Field.DTLS_RPK.field, Hex.encodeHexString(device.securityInfo().getRawPublicKey().getEncoded()));
            }
            stmt.setBoolean(Field.DTLS_X509.field, device.securityInfo().useX509Cert());
            stmt.executeUpdate();
        } catch (SQLiteException e) {
            if (e.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY) {
                throw new UniqueConstraintViolationException(Field.LWM2M_ENDPOINT.name());
            }
            throw new IllegalStateException(e);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private Device mapResultSetToDevice(ResultSet rs) throws SQLException {
        // map the security info
        SecurityInfo secInfo;
        var endpoint = rs.getString(Field.LWM2M_ENDPOINT.name());

        var pskStr = rs.getString(Field.DTLS_PSK.name());
        var pskRpk = rs.getString(Field.DTLS_RPK.name());
        var pskX509 = rs.getBoolean(Field.DTLS_X509.name());

        if (pskStr != null && !pskStr.isBlank()) {
            secInfo = SecurityInfo.newPreSharedKeyInfo(endpoint, rs.getString(Field.DTLS_PSK_ID.name()), HexFormat.of().parseHex(pskStr));
        } else if (pskRpk != null && !pskRpk.isBlank()) {
            secInfo = null; // TODO: convert SecurityInfo.newRawPublicKeyInfo(endpoint, pskRpk); x/y/params/blabla: looks complicated :) https://github.com/eclipse-leshan/leshan/blob/9fc2e3c2fe6675ad9c7b8d77862526073ee3b581/leshan-lwm2m-server-redis/src/main/java/org/eclipse/leshan/server/redis/serialization/SecurityInfoSerDes.java#L114
        } else if (pskX509) {
            secInfo = SecurityInfo.newX509CertInfo(endpoint);
        } else {
            // no security
            secInfo = null;
        }
        return new Device(endpoint, rs.getString(Field.NAME.field), secInfo);
    }
}
