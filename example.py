# an example usage script of the Java autoCommands

ce = cmdExec("<username>", "<hostname/ip>", "<password>")

ce.connectSession()

cmds = ["users | wc -l", "ps -ef | wc -l"]

for cmd in cmds:
	ce.setCommand(cmd)
	ce.runCommand()
	return_value = ce.getReturnVal()
	print return_value.strip()

ce.closeSession()