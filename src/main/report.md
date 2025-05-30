## Section 1: `start()` vs `run()`

### Code

```java
public class StartVsRun {    
    static class MyRunnable implements Runnable {    
        public void run() {    
            System.out.println("Running in: " + Thread.currentThread().getName()); 
        }    
    }    
    public static void main(String[] args) throws InterruptedException {    
        Thread t1 = new Thread(new MyRunnable(), "Thread-1");    
        System.out.println("Calling run()");    
        t1.run();    
        Thread.sleep(100);    

        Thread t2 = new Thread(new MyRunnable(), "Thread-2");    
        System.out.println("Calling start()");    
        t2.start();    
    }  
}
```
### Questions: 
### 1- What output do you get from the program? Why?

**the output :**

```
Calling run()
Running in: main
Calling start()
Running in: Thread-2
```

- when we call the `run()` method, it DOESN'T start a new thread. it simply calls the `run()` method in the 
current thread which is the main thread. Therefore, the output from `t1.run()` is:

`Running in: main`
- But when we call the `start()` method, it DOES start a new thread 
and then calls the `run()` method for the new thread which is Thread-2. Hence, the output is:

`Running in: Thread-2`

### 2- What’s the difference in behavior between calling start() and run()?

- `run()`: Directly invokes the `run()` method on the current thread. It does not create a new thread. The code inside `run()` executes in the same thread that called it.
- `start()`: Creates a new thread and then calls the `run()` method in that new thread. This allows the code in `run()` to execute concurrently with the thread that called `start()`.

---

## Section 2: Daemon Threads

### Code

```java
public class DaemonExample {    
    static class DaemonRunnable implements Runnable {    
        public void run() {    
            for(int i = 0; i < 20; i++) {    
                System.out.println("Daemon thread running...");    
                try {    
                    Thread.sleep(500);    
                } catch (InterruptedException e) {    
                 //[Handling Exception...]  
                }            
            }    
        }    
    }    
    public static void main(String[] args) {    
        Thread thread = new Thread(new DaemonRunnable());    
        thread.setDaemon(true);    
        thread.start();    
        System.out.println("Main thread ends.");    
    }  
}  
```
### Questions:
### 1- What output do you get from the program? Why?
Likely output:

`Main thread ends.`

- In java, daemon threads DO NOT keep the program running. When the main thread which is a 
non-daemon thread finishes, the JVM shuts down and kills any running daemon threads.
- Since the main thread finishes almost immediately after starting the daemon thread, the daemon 
thread is terminated abruptly.
- As a result, we typically only see:

`Main thread ends.`

- Sometimes, if the daemon thread runs fast enough before the JVM shuts down, we might see a 
few lines like:

`Daemon thread running...`

but this is unpredictable and not guaranteed.

### 2- What happens if you remove thread.setDaemon(true)?
- If we remove `thread.setDaemon(true)`, then the thread becomes a normal (non-daemon) thread.

#### Behavior Change:

- Without `thread.setDaemon(true)`, the new thread is a user thread.
- The JVM will wait for all user threads to finish before exiting.
- so the `DaemonRunnable` will run its entire loop.

### 3- What are some real-life use cases of daemon threads?
#### Here are some common real-world use cases:

##### 1. Garbage Collection:

- The JVM itself uses daemon threads to run the garbage collector, which automatically frees 
memory that's no longer in use.

#####  2. Background Cleanup Tasks:

- For example, periodically deleting temporary files or clearing expired sessions in web servers.

##### 3. Monitoring and Logging Services:

- Threads that monitor system health or log system performance can run as daemons.
- These don't need to block the application from shutting down.

---

## Section 3: A shorter way to create threads

### Code 

```java 
public class ThreadDemo {  
    public static void main(String[] args) {  
        Thread thread = new Thread(() -> {  
            System.out.println("Thread is running using a ...!");  
        });  
  
        thread.start();  
    }  
}   
```
### Questions:
### 1- What output do you get from the program?

the output:
`Thread is running using a ...!`

### 2- What is the `() -> { ... }` syntax called?
The syntax `() -> { ... }` is called a lambda expression in Java.

### 3- How is this code different from creating a class that extends Thread or implements Runnable?
Using lambda expression:

- Short and concise
- No need to create a separate class
- Good for one-time, simple tasks
- Not suitable for complex or reusable logic

Using a Class that Implements `Runnable`:

- Reusable logic
- Better for larger or structured code
- requires a separate class

Using a Class that Extends `Thread`:

- we can override other `Thread` methods
- Less flexible

so in conclusion, the lambda expression is a simple, one-time thread logic.