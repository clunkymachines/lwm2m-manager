@import java.util.List
@import com.clunkymachines.lwm2m.manager.model.Device
@param List<Device> devices
<!DOCTYPE html>
<html lang="en">
@template.shared.head()
<body>
@template.shared.header()
    <div class="content">
@template.shared.menu()
        <div class="main">
                <h1>Device List</h1>
                <table>
                    <thead data-theme="light">
                        <tr>
                            <th>LWM2M Endpoint</th>
                            <th>Name</th>
                            <th>DTLS PSK ID</th>
                        </tr>
                    </thead>
                    <tbody>
                    @for(Device device : devices)
                        <tr>
                            <td>${device.endpoint()}</td>
                            <td>${device.name()}</td>
                            <td>${device.securityInfo().getPskIdentity()}</td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
                <a href="device/add" role="button">Create Device</a>
        </div>
    </div>
</body>
</html>