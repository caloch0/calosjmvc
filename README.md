# CaloJMVC
An MVC project in java, my custom MVC project for use


I planned on making the project a general mixture of MVC framework that uses all the java basises I have mastered; 
It makes use of the java exchange server and thread pool, and use url parameters as routes to create controller objects;
Also integrated all utilities; I will make this a maven project later on;

Of course, I may switch to use existing frameworks such as springboot etc. in actual practices, but this one is to gain general mastery,,




Todos:
1. performance tuning, load test
2. fly weight for system created objects to avoid too many instantiation costing too much memory such as, satisfact, handler, some objects should be singleton.
3. business logic caching, etc.


Todos: 2024
1. extracted IMysqlDbContext for testability when configuration is set to not add dbcontext and make all usages to declare with interface
2. created a default MySqlDbContext that implements the IMysqlInterface with default or empty method body for tests to run through without real db connection
3. check request header contentType, if application/json, read the request body from stream, and deserialize the string body with object parser


