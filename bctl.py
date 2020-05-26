#!/usr/bin/python3.5

import subprocess
import shlex

print("Enter borrego command:")

commands = ["pull latest", "build", "run", "ls", "net", "start", "stop", "rm","quit", "q"]

def shcmd(cmdStr):
    cmd = shlex.split(cmdStr)
    subprocess.run(cmd)

loop = True
while(loop):
    arg = input("Command: ")
    args = shlex.split(arg)

    if(arg == "pull latest"):
        print("Pulling latest borrego image from Docker Hub")

    elif(arg == "images"):
        print("Images:")
        subprocess.run(["docker", "image", "ls"])

    elif(arg == "ls"):
        subprocess.run(["docker", "container", "ls", "-a"])

    elif(arg == "net"): 
         shcmd("docker network create --subnet=172.18.0.0/16 borrego")

    elif(args[0] == "run"):

        cmd = shlex.split("docker container rm borregorun")
        subprocess.run(cmd)
        cmdStr = "docker run -d -t -i "
        cmdStr = cmdStr + "--name borregorun "
        cmdStr = cmdStr + "-p 6363:6363 -p 19026:19026 "
        cmdStr = cmdStr + "borrego:r7"
        shcmd(cmdStr)

    elif(args[0] == "start"):
        cmdStr = "docker start " + "borregorun"
        shcmd(cmdStr)
        cmdStr = "docker exec borregorun /bin/sh -c \'/app/borrego_init.sh "
        cmdStr =  cmdStr + args[1] + "\'"
        shcmd(cmdStr)

    elif(args[0] == "stop"):
        cmdStr = "docker stop " + "borregorun"
        shcmd(cmdStr)

    elif(args[0] == "rm"):
        cmdStr = "docker container rm  " + "borregorun"
        shcmd(cmdStr)
    
    elif(arg == "quit"):
        exit()

    elif(arg == "q"):
        exit()         

    else:
       print(commands) 
