<!DOCTYPE html>
<html lang="en">
@template.shared.head()
<body>
@template.shared.header()
    <div class="content">
@template.shared.menu()
        <div class="main">
            <h1>Create a device</h1>
            <main class="container">
                <form action="/device" method="POST">
                    <div>
                        <label for="endpoint">Endpoint<input type="text" id="endpoint" name="endpoint" placeholder="123456"></label>
                        <label for="name">Name<input type="text" id="name" name="name" placeholder="device name"></label>
                        <label for="pskid">Pre-Shared-Key Identifier<input type="text" id="pskid" name="pskid" placeholder="Pre-Shared-Key Identity"></label>
                        <label for="psk">Pre-Shared-Key as hexadecimal (ex: "01BCEF21") <input type="text" id="psk" name="psk" placeholder="Pre-Shared-Key"></label>
                        <input type="submit" value="Create" role="button"/>
                    </div>
                </form>
            </main>
        </div>
    </div>
</body>
</html>