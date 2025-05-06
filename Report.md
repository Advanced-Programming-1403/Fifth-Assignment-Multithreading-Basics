
**Q1**
run()keeps execution in the main thread , while start() creates new threads .
_output with run()_
Calling run
Running in : main

_output with start()_
Calling run
Running in : Thread_2
or
Running in :Thread_2
Calling run 

_In the first case, only a Thread is created, but the method runs in the main thread._
_In the second case, a Thread is created and the run method is executed within that new thread._
_Overall, the first case runs sequentially, while the second runs concurrently._
**ًQ2**
A daemon thread is outomaticalliy terminated by the JVM when all normal threads are terminated .
Daemon threads often use for background tasks. 
If we remove Thread.setDaemon(true) the programe run 

_output with daemon(true)_
Main thread ends
Daemon thread is running...

_output without daemon(true)_
Main thread ends
Daemon thread is running...(+20)

_When the program runs without daemon mode, even after the main tasks are finished, the program continues executing the daemon method completely (all 20 times), and only then terminates._
_However, daemon threads are used for non-critical background tasks and should not prevent the program from closing._
_For example, an auto-save system that saves every 5 seconds can be a daemon thread, while manual save triggered by Ctrl+S should be handled by a main (non-daemon) thread._
_It doesn’t matter if the daemon thread finishes its task when the program is closed by the user._

Some examples of daemon systems include:

Auto-save systems

Background loggers

Cache cleaners

Idle resource monitors

Heartbeat/ping checkers for servers


**Q3**
{...}<- () lambda Eexprision

_When creating threads in Java, there are three common approaches: extending the Thread class, implementing the Runnable interface, or using a lambda expression with Runnable. Extending Thread means you can’t inherit from any other class, which limits flexibility, but it's suitable when you need to define a custom thread with special behavior. Implementing Runnable is more flexible, as it allows you to inherit from other classes and separates the execution logic from the thread itself, making the code more reusable and better organized. Using a lambda expression is the most concise and readable method, especially for short, simple tasks. However, it’s not ideal for complex logic or reusable components. Overall, Runnable with lambda offers simplicity, Runnable with a class provides structure and reusability, and extending Thread works best for highly customized thread behavior._

_output_
Thread is running using a ...!








