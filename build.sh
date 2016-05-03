
javac -cp '.:libs/*' cmdExec.java

jar -cfm sshClient.jar Manifest.txt *.class

#java -cp '.:libs/*' cmdExec