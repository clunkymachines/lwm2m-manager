<!DOCTYPE html>
<html lang="en">
@template.shared.head()
<body>
        <script type="text/javascript">
            @raw
            // on form post error, will modify the form to set the status
            document.addEventListener('htmx:responseError', evt => {
            const xhr = evt.detail.xhr;

            if (xhr.status == 422) {
                    const errors = JSON.parse(xhr.responseText);

                for (const formId of Object.keys(errors)) {
                    const formErrors = errors[formId];
                    for (const name of Object.keys(formErrors)) {
                        const field = document.querySelector(`#${formId} [name="${name}"]`);
                        field.setCustomValidity(formErrors[name]);
                        field.addEventListener('focus', () => field.reportValidity());
                        field.addEventListener('change', () => field.setCustomValidity(''));
                        field.reportValidity();
                    }
                }
            } else {
                // Handle the error some other way
                console.error(xhr.responseText);
            }
            });
            @endraw
        </script>
@template.shared.header()
    <div class="content">
@template.shared.menu()
        <div class="main">
            <h1>Create a device</h1>
            <main class="container">
                <form id="create-device" hx-post="/device"
                 hx-on::after-request="if(event.detail.successful) window.location.href = '/device'">
                    <div hx-target="this" hx-swap="outerHTML">
                        <label for="lwm2m_endpoint">Endpoint<input type="text" id="lwm2m_endpoint" name="lwm2m_endpoint" placeholder="123456"></label>
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