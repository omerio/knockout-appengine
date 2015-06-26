# knockout-appengine

Knockout-appengine is a [Google App Engine](https://cloud.google.com/appengine/docs) backend for this Knockoutjs example [https://github.com/omerio/knockout-app](https://github.com/omerio/knockout-app).

**Source**: [github.com/omerio/knockout-appengine](https://github.com/omerio/knockout-appengine)

**Author**: [Omer Dawelbeit](http://omerio.com)


## Live Demo

[http://knockout-app.appspot.com/](http://knockout-app.appspot.com/)

## Jump start

To run or deploy the application:
```bash
    git clone https://github.com/omerio/knockout-appengine.git
    cd knockout-appengine
    mvn install
    #to test it locally:
    mvn appengine:devserver
    #or to deploy it:
    mvn appengine:update
```    

To deploye to App Engine you need to change the following values with your own:

- App Engine app-id (`<appengine.app.name>knockout-app</appengine.app.name>`) in pom.xml 

If you don't already have a cloud project you need to create one here [https://console.developers.google.com/start](https://console.developers.google.com/start) and click 'Create an empty project' link.

### Local URLs:
- Customer Admin: [http://localhost:8888/](http://localhost:8888)
- Datastore: [http://127.0.0.1:8888/_ah/admin/datastore?kind=Customer](http://127.0.0.1:8888/_ah/admin/datastore?kind=Customer)

