# autoCommands

A small Java program that uses JSch and Jython for running SSH commands

It can be run either manually or with command line arguments to execute a Jython script.

Currently it's not all that super-duper, I only needed it to return numbers, so that's all does really. It can return small strings but nothing too crazy as of yet.

Pull requests and comments welcome.

## Usage

Run a Script:

	sshClient.jar -s <path/to/script.py>

Arguments:

	-u username
	-h hostname
	-p password

sshClient.jar -u <username> -h <hostname> -p <password>

or just start in manual mode by running the jar

## Requirements

 - JSch
 - Jython standalone