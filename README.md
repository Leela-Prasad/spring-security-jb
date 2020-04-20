Spring Security add a layer on top of your applicaiton inspecting every request by asking 2 questions.
1. who are you
2. what do you want to do

Security is a common concern but the way every application secures is different, to handle this spring security comes with lot of customisation and flexibility.

Spring security handles most common vulnerabilities out of the box like
session fixation
click jacking
cross site request forgery.

We can do below things like
login and logout functionality.
Allow/Block access to URLs to logged in users.
Allow/Block access to URLs to logged in users AND with certain roles.
Method Level Security.


Spring Security provides support for below use cases
Username/password authentication
App Level Authorisation
SSO/Okta/LDAP
Inter App Authorisation like OAuth
Micro service Security(using tokens, JWT)


Spring Security Jargons
Authentication - Answering the question “who are you”
Authorization - can this user do this?
Principal
Granted Authority
Roles


Authentication
This is to answer the question “who are you?”

This Authentication can be Knowledge based like
Password
Pin code
Answer to secret/personal question.

Knowledge Based Authentication is easy to implement but Not Fully Safe.


We can have Possession Based Authentication like
Phone/Text messages
Key cards and badges
Access token device

We can also have Multi Factor Authentication = Knowledge Based + Possession Based Authentication.

Authorization
This is all about answering the question “Can this user do this?” - Yes/No

Principal - is the currently logged in user

Granted Authority:
In Spring Security a specific permission is called Granted Authority.

Store Clerk has following authorities.
do_checkout
make_store_announcements

Department Manager has following authorities.
do_checkout
make_store_announcements
view_department_financials
view_department_inventory 

Store Manager has following authorities.
do_checkout
make_store_announcements
view_department_financials
view_department_inventory 
view_store_financials


Roles:
Usually Granted Authority are fine graded
we can have 5 clerks and we need assign authorities to all these 5 clerks
It is tedious to maintain, for this we will group these authorities and this grouping is called Role.

Role is a Group of Authorities.
Roles are coarse-grained permissions



Adding Spring Security to Spring Boot Application:
for this we need to add following dependency.

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>

As soon as we add this dependency Spring Security does the following 
Adds mandatory Authentication for URLs
Adds Login Form
Handles Login Error
Creates a user and sets a default password.

This is happening because spring security configures a filter for our application and will intercept every request.

The default user name and password is 
username: user
password: <It will print a random password on the console for every restart>

To override this we can define below properties in application.properties
spring.security.user.name=foo
spring.security.user.password=foo


Authentication:
We can customise Authentication by affecting AuthenticationManager 

AuthenticationManager in Spring Security manages authentication part by returning success or failure
we have a method authenticate() in Authentication Manager.

To configure this AuhtenticationManager we have to do below steps
1. Get hold of AuthenticationManagerBuilder
2. set the configuration on it like in memory, jdbc or ldap etc.

After configuring AuthenticationManagerBuilder an AuthenticationManager object will be created inside the application with the configuration that we set in AuthenticationManagerBuilder.


How to get AuthenticationManagerBuilder?
In Spring Security there is a class (WebSecurityConfigurerAdapter) which will have a configure method with AuthenticationManagerBuilder as an argument.

So we can extend this class and override this method to provide custom configuration on AuthenticationManagerBuilder
If we don’t do this then the default Configurer class will be invoked during the life cycle calls and will do the form based login that we seen by adding the spring-security-starter in the pom file.

Authorization:
Similar Authentication if we want to customise Authorisation then we need to get hold HttpSecurity Object

This is done by Extending a class (WebSecurityConfigurerAdapter) which will have a configure method with HttpSecurity as an argument.
So we can extend this class and override this method to provide custom configuration.



How spring security is able to intercept every request coming to the application?
This is happening because of the filters concept.
Spring Security configures below filter when a spring boot starter dependency is added to the class path which intercepts all requests.


<filter>
  <filter-name>springSecurityFilter</filter-name>
  <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>

<filter>
  <filter-name>springSecurityFilter</filter-name>
  <url-pattern>/*</url-pattern>
</filter>


This Delegating Filter will not do all the job instead it delegates to bunch of other spring security filters that do different things on the url that is requested or the configuration that we did for the app. 
one of the filter that comes with spring security is authentication filter which initiates authentication process and we have several authorisation filters as well. 


Generally for Authentication operation we will give credentials as input and output is principal instead of success, because it keep all the logged in user information in this principal object.

Here Authentication is an Interface which will store user credentials before authentication and principal after authentication successful.

How different Authentication Mechanisms(like credentials/LDAP/Oauth) work with Spring Authentication?
In Spring Security we have an Interface called AuthenticationManager 
AuthMgr just delegates the authentication process to one of the Authentication Providers that implements AuthMgr (This implements Provider Pattern).

Authentication Providers have 2 methods
1. authenticate() - for doing authentication
2. supports() - to check which Authentication Type it can support.


If we want to authenticate a user then we need to get the user information either from datastore or LDAP Record and then verifies whether credentials are matching or is it locked or password expired 

This is a common process for every authentication provider where authentication takes place.


So spring has a predefined class for this process.
UserDetailsService - class
loadUserByUsername() 

After Authentication is successful then it will store the Security Context in the thread local so that it can access any details that are required for that request.

If the Authentication is not successful in that cause Authentication Provider will throw an Authentication exception and this exception bubbles upto the user.

There is a security Context that is associated with the current thread. The Authentication object will be put into this security context and this will be put in the thread local to use in authorisation, or to know who is current user

How Authentication is remembered across Requests?
There is one more filter in spring security to do this job where it will allow security context to be associated with the user session.


What to do When Default Authentication Provider is not available in Spring Security?
Eg: JPA doesn’t have Default Auth Provider, so we need to write our own logic for this
If we see other providers, what they do is load the users and return the user information to the Authentication Manager.
i.e., loadUserByUserName() in UserDetailsService.

So happy part is we don’t need to write our Provider class instead we can provide implementation for UserDetailsService and Spring Security is going to trust this service and do the authentication.


JWT(Json Web Tokens)
JWT is one of the popular Authorisation mechanism between services.

JWT created a standard way for two parties to communicate securely.

Http is a stateless protocol means every interaction in a http need to contain all information needed for that interaction.

Below are the popular Authorisation mechanisms in web applications:
Session Token
JSON Web Token

Every time we send a request to the service we need to give the endpoint and (token) to recognise who is doing that, but for a web application we don’t see this mechanism when we  access different pages that is because session cookie/token is exchanged between client and server automatically through headers.

This mechanism is good but it has following cons
1. session object needs to be stored in the server.
and the implications comes when there are multiple servers behind Load Balancer means if the first request goes to server1 then session object will be created and if the next request to server2 then server2 doesn’t have any idea about this client as it doesn’t have session token/cookie in that server.

To avoid this we can have central Caching Server like Redis that saves session tokens and each server can lookup session from the cache.

Another approach is to use sticky sessions in the load balancer means LB knows about which request should route to which server, but this is not scalable atleast in case of micro services architecture.


** Another Alternative is to use JSON Web Tokens
In this approach we will have all the details in the client side and the client need to produce that for every interaction, so it will reduce load on the server side.

Session Token is a Reference Token
Json Web Token is a Value Token
because session id will have a reference to the session object in the server
whereas in JWT it contains all the details like headers,payload,signature  so no information at the server end, hence it is value token

For Every Interaction with the server with JWT server will check whether signature is valid and then allow the client to interact with the service.


JWT can also be stored as a cookie and make the browser send that cookie in the header automatically for every request.

Sample JWT
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c


Every JWT contains 3 parts separated by “.”
1. Header
2. Payload
3. Signature

Header:
{
  "alg": "HS256",
  "typ": "JWT"
}


Payload:
{
  "sub": "1234567890",
  "name": "John Doe",
  "iat": 1516239022
}

Signature:

HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  your-256-bit-secret

) secret base64 encoded

Header contains about the type of authorization and Algorithm for Hashing.

Payload is the actual content that we need to sent.

Signature is base64(Header) + . + base64(payload) + secret key

This Secret key is stored on the server and no one can stole.

So while sending the JSON Web token if some information is manipulated in the payload then  he cannot generate the signature for that modification as secret key is there on the server, which makes the JWT invalid.

In this Authorization mechanism payload is open to everyone but no one can tamper/modify the content similar to the client certificate.

Here Base64 is just an encoding option which will be used to encode special characters.
Base64 is nothing to deal with security.


JWT Process:
1. client has to authenticate with the server using any authentication mechanism then the server checks who they are and make a decision to allow the user or not.
2. once authentication is successful then server creates a JWT and send it to the client.
3. client can save this jwt in local storage or cookie and can send this JWT in the Http Header in the below format.
Authorisation: Bearer JWT-Token
4. Now for each request server will first verify the signature whether it is valid or not
5. If the signature is valid then it will decode base64 payload and process the request.

Disadvantages of JWT:

How secure is a JWT if it is readable by anyone?
No sensitive information like credentials, tax id,dob


What if someone steals my JWT and use it themselves?
transcations with JWT should happen in HTTPS.

How do you disable a JWT?
incase of Http Session we can do a logout and the session on the server will be destroyed or invalidated.
But in case of JWT everything is there with the client, so in this case we can setup expiry for the JWT and  a work around is we can maintain a black listed JWT on the server so the every request will check whether the incoming JWT is there in black listed JWT or not and process the request.

JWT Authentication.
Steps:
Create a new Authentication API endpoint
Examine every incoming request for valid JWT and authorise

Step1:
Expose /authenticate endpoint which will accept username and password, if it is valid then it will return JWT Token.

Step2:
Intercept all incoming requests.
For this we will write a spring filter where we will extract JWT token from the header, validate the token, if it valid then we will set this token in security context holder.

Setting this Authentication Object in the Security Context Holder is mandatory so that Authentication will be skipped in the next Authentication Filter.

*** We need to add our custom filter before Authentication Filter so that our custom filter will load/set Authentication object in security context holder so that authentication will be skipped in Authentication Filter. 


Hashing:
Generally Passwords will not be stored in plain text in the systems.

Password Encryption and Decryption is also Not Recommended solution as of today, because server needs to store the secret key that will be used for encryption and decryption which can possibly hacked by hacker.

So the solution is Hashing.
Hashing is a process making a plain text into a scrambled or un recognised text.

Properties of Hashing:
1. Repeatable
2. One way function

If you give the same text it should always produce same scrambled text.
One way function means there is no way to reverse the scrambled text into plain text.

Generally when a user enters details, passwords will be hashed and stored in the system.

When user is trying to login into the system then system will compute the hash for the entered password and compares this hash value with the existing hash value in the DB.

Popular Hashing Functions
1. MD5
2. SHA
3. BCrypt


OAuth:
OAuth is a standard created to access a service on behalf of the user. 
It is used for Authorization between services.
OAuth Versions
OAuth 1.0
OAuth 2.0 (most widely used)

*** In this OAuth we can give Access to the service with LIMITED Access or Access that is needed for the service to do the job.
e.g.: To share a photo from Google drive we will not give entire access Google Account like Email, Youtube, Drive etc.
we will give a specific access which service is requesting.


** OAuth is just Access Delegation

OAuth Flow 
When a third party application is asking to access some services on another applicaiton(like google drive) then google drive will redirect a screen to the user saying this is the third party application which is requesting access for the following services.

If the user verifies and approves then a token will be created and given to the third party application.

This Token contains information about the Third party details, user information and services which it is requested for, and this token should not be tampered.

For this reason we use JWT as the token generation mechanism.


OAuth Terminologies:
1. Resource - It is the protected resource(photo) which another service is trying to get access.

2. Resource Owner - It is the user who will grant access to the service to access the resource.
An entity capable of granting access to a protected resource.

3. Resource Server - It is the server(google drive) hosting the protected resources(photo).

4. Client - It is the application which needs access to the protected resource on behalf of resource owner and with owner authorization
An application making protected resource requests on behalf of the resource owner and with its authorisation.

5. Authorisation Server - It is the server Issuing access tokens to the client.

Who has the burden of Security in this OAuth Flow?
It is the Resource Server who is hosting the protected resources.

To separate the concerns of Issuing tokens to the client application and securing the protected resources, we have one more Entity called Authorization Server.

This Authorisation Server whole responsibility is to verify client if it trusted or not and issues Tokens to the client application where as Resource Server will validate the token and provide access to the protected resource.

Generally Resource Server will be coupled with Authorisation server.
This Authorisation server can be along with Resource Server or it can be a separate server . 


OAuth Flow1 - Authorization code Flow
Steps:
1. Resource Owner makes a call to the photo printing service(client) for printing the photos, but photos are there in Google Drive(Resource Server).
2. For getting access to the resource it needs to get access token, for this it contacts Authorisation server(which will issue tokens) for the access token, but the Authorisation server will not believe the photo printing service.
3. Authorisation Server contacts Resource Owner stating this the application(photo printing service) which requested access for these resources, do you want to approve?
4. Client Reviews and approves the request.
5. Then Authorisation Server will share an Auth token(short lived token) to validate the client.
6. Photo printing service will send the Auth token so that exchange happen between Authorisation service and photo printing service.
7. Authorization service now trusts the service and issues an Access Token.
8. Now Photo Printing Service will use this Access token to get the resource from the Resource Server.
9. Resource Server will share the requested resource to the photo printing service.

*** This is the complete and safest Flow in OAuth.


OAuth Flow2 - Implicit Flow
Steps:
1. Resource Owner makes a call to the photo printing service(client) for printing the photos, but photos are there in Google Drive(Resource Server).
2. For getting access to the resource it needs to get access token, for this it contacts Authorisation server(which will issue tokens) for the access token, but the Authorisation server will not believe the photo printing service.
3. Authorisation Server contacts Resource Owner stating this the application(photo printing service) which requested access for these resources, do you want to approve?
4. Client Reviews and approves the request.
5. Authorization service issues an Access Token.
6. Now Photo Printing Service will use this Access token to get the resource from the Resource Server.
7. Resource Server will share the requested resource to the photo printing service.

*** In this Flow client(photo printing service) and the Authorisation server exchange is not happened and what happens if the access token is misused, so that fake service can make a call to the Resource Server. So this is not that secured when compared to Step1 - Authorisation Code Flow.

This Flow is used when the access tokens are short lived like java script applications running on the browser to get access to some resource from the resource server.


OAuth Flow3 - Client Credentials
This Flow is used when the client is well trusted (confidential/internal clients)
This Flow is most commonly used for Authorization between Microservices.

Steps:
1. Micro service 1 makes a call to Authorization Server for the access token.
2. Authorization Server issues access token to Micro service 1.
3. Micro service 1 uses access token to access an api which Micro service 2 is exposing.
4. Micro service 2 is giving back the result.
