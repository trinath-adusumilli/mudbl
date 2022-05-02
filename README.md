#Twitter data visualization
This is a backend application for twitter data visualization website.
### Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)

### Cloning application on server
```git clone https://github.com/trinath-adusumilli/mudbl.git```


### Deployment steps
- Build the application
```mvn clean install```
  
- Start the application
```mvn spring-boot:run```
  
- Check the status of the application
```http://<Host Address>/actuator/health```
  - The above command should give the following response 
    ```{"status": "UP"}```
    

    

  
