@import java.util.List
@import org.eclipse.leshan.server.registration.Registration
@import com.clunkymachines.lwm2m.manager.ui.data.DataObject
@import com.clunkymachines.lwm2m.manager.ui.data.DataObjectInstance
@import com.clunkymachines.lwm2m.manager.ui.data.DataResource
@param  Registration reg
@param  List<DataObject> objects
<!DOCTYPE html>
<html lang="en">
@template.shared.head()
<body>
@template.shared.header()
    <div class="content">
@template.shared.menu()
        <div class="main">
                <h1>Device Details</h1>
                <div  hx-ext="sse" sse-connect="/event?ep=${reg.getEndpoint()}"> 
                @for(var obj : objects)
                  <details>
                    <summary>${obj.name()} ${obj.id()} ${obj.version()}</summary>
                    ${obj.description()}

                    @for(var instance : obj.instances())
                      <h3>Instance: ${instance.id()}</h3>
                      <table>
                        <thead data-theme="light">
                          <tr>
                            <th>Name</th>
                            <th>ID</th>
                            <th>Operations</th>
                            <th>Last value</th>
                          </tr>
                        </thead>
                        <tbody>
                        @for(var resource: instance.resources())
                        <tr>
                          <td>${resource.name()}</td>
                          <td>${resource.id()}</td>
                          <td>
                          @if(resource.read())
                            <button hx-swap="none" hx-get="/req?type=read&endpoint=${reg.getEndpoint()}&path=/${obj.id()}/${instance.id()}/${resource.id()}">R</button>
                          @endif
                          @if(resource.write())
                            <button>W</button>
                          @endif
                          @if(resource.execute())
                            <button>X</button>
                          @endif
                          </td>
                          <td>
                              <div sse-swap="${reg.getEndpoint()}/${obj.id()}/${instance.id()}/${resource.id()}"></div>
                          </td>
                        </tr>
                        @endfor
                        </tbody>
                      </table>
                    @endfor
                  </details>
                  <hr />
                @endfor
                </div>
        </div>
    </div>
</body>
</html>
