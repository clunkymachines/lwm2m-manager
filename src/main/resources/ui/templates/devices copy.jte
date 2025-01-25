@import java.util.List
@import com.clunkymachines.lwm2m.manager.model.Device
@param List<Device> devices
<!DOCTYPE html>
<html lang="en">
@template.shared.head()
<body>
    <nav>
        <ul>
            <li><h2>LWM2M Manager</h2></li>
        </ul>
        <ul>
        <li><a href="#" role="button">My Profile</a></li>
        </ul>
    </nav>
    <aside>
    <nav>
        <ul>
            <li><button class="secondary">Devices</button></li>
            <li><a href="#">Firmware</a></li>
            <li><a href="#">Users</a></li>
        </ul>
    </nav>
    </aside>
    <header class="container">
        <h1>Device List</h1>
    </header>
    <main class="container">
        <table class="striped">
            <thead data-theme="dark">
                <tr>
                    <th>LWM2M Endpoint</th>
                    <th>Name</th>
                    <th>DTLS PSK ID</th>
                    <th>DTLS PSK</th>
                </tr>
            </thead>
            <tbody>
            @for(Device device : devices)
                <tr>
                    <td>${device.endpoint()}</td>
                    <td>${device.name()}</td>
                    <td>${device.dtlsPskId()}</td>
                    <td>${device.dtlsPsk()}</td>
                </tr>
            @endfor
            </tbody>
        </table>
        <a href="device/add" role="button">Create Device</a>
    </main>
</body>
</html>