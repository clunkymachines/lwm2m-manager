CREATE TABLE migration (
    script_name TEXT PRIMARY KEY CHECK (script_name <> ''), -- Name of the script
    execution_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Date and time of execution
);

CREATE TABLE device (
    lwm2m_endpoint TEXT PRIMARY KEY CHECK (lwm2m_endpoint <> ''), -- The LWM2M endpoint
    name TEXT CHECK (name <> ''), -- Name of the device, provided by the user
    dtls_psk_id TEXT, -- The DTLS PSK Identity as text
    dtls_psk TEXT, -- The DTLS Pre-shared-key as hexadecimal text (empty if not allowed)
    dtls_rpk TEXT, -- The DTLS Raw-Public-Key as hexadecimal text (empty if not allowed)
    dtls_x509 BOOLEAN -- does X.509 authentication is allowed for this device
);

CREATE INDEX idx_device_dtls_psk_id ON device(dtls_psk_id);