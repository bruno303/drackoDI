# DrackoDI

DrackoDI is a simple container for dependency injection with the goal to be used in small projects where all the overhead of big frameworks are not necessary.
This project is based on how the [.NET Microsoft.Extensions.DependencyInjection](https://www.nuget.org/packages/Microsoft.Extensions.DependencyInjection) works:
* You have a dependency Container where you register all classes that you want to be managed by the container.
* You "build" the container, and the result will be a BeanProvider where you can ask for the beans already registered.
* Once a container is builded, it will not accept any more classes to be registered.

DrackoDI provides two lifecycles for the beans:
* Singleton, where only one instance will be used everywhere. When you do not tell which lifecycle have to be used, singleton is the default.
* Transient, where new instances will be created everytime you ask for a bean of that type.

You can register multiple beans of the same type, but you need to give them names and set one as primary.
If you have multiple beans of the same type registered and ask for this type of bean without specify the name, the primary bean will be returned.
Or you cann ask for the bean and pass it's name.

The dependency injection works only by constructor injection, and must be only one constructor in the class.
There are exceptional cases where you can use a custom function to create a bean. In this case you can call any constructor you want.

## Build from souce

### Pre-requisites

* Gradle >= 6.8
* Java 11 
* Lombok >= 1.18.20

### Full build
```shell
$ ./gradlew clean build
```

### Build skipping tests
```shell
$ ./gradlew clean build -x test -x check
```

or you can run:
```shell
$ ./build-for-release.sh
```

---

## Usage examples

* simple use case

```java
public class Dummy {
    public String hello() {
        return "Hello World";
    }
}

public class Application {
    public static void main(String[] args) {
        //... code
        Container container = new DefaultContainerImpl();
        container.register(Dummy.class);
        BeanProvider beanProvider = container.build();
        Dummy dummy = beanProvider.getBean(Dummy.class);
        //... use dummy bean as you wish
    }
}
```

* Register two classes where one depends on another
```java
public class Dependency {
    //... properties, getters and setters
}

public class Dummy {
    private final Dependency dependency;
    
    public Dummy(Dependency dependency) {
        this.dependency = dependency;
    }
    
    public String hello() {
        return "Hello World";
    }
}

public class Application {
    public static void main(String[] args) {
        //... code
        Container container = new DefaultContainerImpl();
        container.register(Dummy.class);
        container.register(Dependency.class);
        BeanProvider beanProvider = container.build();
        Dummy dummy = beanProvider.getBean(Dummy.class);
        //... use dummy bean as you wish
    }
}
```

* Register two beans of the same type and use then by name
```java
public class Dummy {
    public String hello() {
        return "Hello World";
    }
}

public class Application {
    public static void main(String[] args) {
        //... code
        Container container = new DefaultContainerImpl();
        container.register(Dummy.class, "dummy1", true); // the boolean flag indicates that this is the primary bean
        container.register(Dummy.class, "dummy2", false); // the boolean flag indicates that this is not the primary bean
        BeanProvider beanProvider = container.build();
        Dummy dummy1 = beanProvider.getBean(Dummy.class); // primary bean returned
        Dummy dummy2 = beanProvider.getBean(Dummy.class, "dummy2");
        //... use dummy beans as you wish
    }
}
```

* Using custom function to create a bean
```java
public class Dummy {
    private final String name;
    public Dummy(String name) {
        this.name = name;
    }
    
    public String hello() {
        return "Hello " + name;
    }
}

public class Application {
    public static void main(String[] args) {
        //... code
        Container container = new DefaultContainerImpl();
        container.register(Dummy.class, beanProvider -> {
            return new Dummy(args[0]); //usings name passed by program args 
        });
        BeanProvider beanProvider = container.build();
        Dummy dummy = beanProvider.getBean(Dummy.class);
        //... use dummy bean as you wish
    }
}
```

* Creating transient beans
```java
public class Dummy {
    public String hello() {
        return "Hello World";
    }
}

public class Application {
    public static void main(String[] args) {
        //... code
        Container container = new DefaultContainerImpl();
        container.register(Dummy.class, Scope.TRANSIENT);
        BeanProvider beanProvider = container.build();
        Dummy dummy = beanProvider.getBean(Dummy.class);
        Dummy dummy2 = beanProvider.getBean(Dummy.class);
        //... use dummy beans as you wish
        // the dummy and dummy2 are two different instances
    }
}
```

* Creating a bean with a list of other bean type

```java
import java.util.ArrayList;

public class Dependency {
    public String hello() {
        return "Hello World";
    }
}

public class Dummy {
    private final List<Dependency> dependencies;

    public Dummy(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public void log() {
        dependencies.forEach(d -> System.out.println(d.hello()));
    }
}

public class Application {
    public static void main(String[] args) {
        //... code
        Container container = new DefaultContainerImpl();
        container.register(Dependency.class, Scope.TRANSIENT, "dep1", true);
        container.register(Dependency.class, Scope.TRANSIENT, "dep2", false);
        container.register(Dependency.class, Scope.TRANSIENT, "dep3", false);
        container.register(Dependency.class, Scope.TRANSIENT, "dep4", false);

        container.register(Dummy.class, Scope.SINGLETON, beanProvider -> {
            Collection<Dummy> depCollection = beanProvider.getBeans(Dependency.class);
            List<Dummy> depList = new ArrayList<>(depCollection);
            return new Dummy(depList);
        });

        BeanProvider beanProvider = container.build();
        Dummy dummy = beanProvider.getBean(Dummy.class);
        dummy.log();
    }
}
```