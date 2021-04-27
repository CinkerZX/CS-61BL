A "bug" from runner.py

After I introduced .jar file (commons-lang3-3.11.jar) into the project, I put the wrong directory( which should be library-su20). 

When I ran .in files, I got the message "Could not find or load main class" in Mandarin and "ERROR (java gitlet.Main exited with code 1)" in English. This is really confusing. 

After lots of efforts finding out what is happening, we finally figure it out that the error happens during compiling files instead of executing functions. 

Then it comes to the "bug" of runner.py, after execute doCompile function and doExecute function, there are two different ways to catch errors:

*if* msg != "OK"     ----------from doExecute

*if* msg.find("error") >= 0   ------------from doCompile

Since our default system language is  Mandarin, the word "error" is shown as "错误". That makes huge difference, right?

