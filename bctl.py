#!/usr/bin/python3

import subprocess
import shlex

print("Enter borrego command:")

commands = ["pull", "images", "ls", "run", "sh", "startfull", "start", "stop","rm", "quit", "q"]

def shcmd(cmdStr):
    cmd = shlex.split(cmdStr)
    subprocess.run(cmd)

loop = True
while(loop):
    arg = input("Command: ")
    args = shlex.split(arg)

    if(arg == "pull"):
        print("Pulling latest borrego image from Docker Hub")
        cmdStr = "sudo docker pull telhoc/borrego-test:latest"
        shcmd(cmdStr)        

    elif(arg == "images"):
        print("Images:")
        cmdStr = "sudo docker image ls"
        shcmd(cmdStr)   

    elif(arg == "ls"):
        cmdStr = "sudo docker container ls -a"
        shcmd(cmdStr)   

    elif(args[0] == "run"):
        cmdStr = "sudo docker container rm borregorun"
        shcmd(cmdStr)   
        cmdStr = "sudo docker run -d -t -i "
        cmdStr = cmdStr + "--name borregorun "
        cmdStr = cmdStr + "-p 6363:6363/tcp -p 6363:6363/udp -p 19026:19026 "
        cmdStr = cmdStr + "telhoc/borrego:latest /bin/bash"
        shcmd(cmdStr)
        cmdStr = "sudo docker stop borregorun"
        shcmd(cmdStr)
        cmdStr = "sudo docker start borregorun"
        shcmd(cmdStr)
        cmdStr = "sudo docker exec -it borregorun /bin/bash"
        shcmd(cmdStr)        

    elif(args[0] == "sh"):
        cmd = shlex.split("sudo docker exec -it borregorun /bin/bash")
        subprocess.run(cmd)

    elif(args[0] == "startfull"):
        cmdStr = "sudo docker start " + "borregorun"
        shcmd(cmdStr)
        cmdStr = "sudo docker exec borregorun /bin/bash -c \'nohup /app/borrego_init.sh "
        cmdStr =  cmdStr + args[1] + " && sleep 4  &\'"
        shcmd(cmdStr)

    elif(args[0] == "start"):
        cmdStr = "sudo docker start borregorun "
        shcmd(cmdStr)
        cmdStr = "sudo docker exec -it borregorun /bin/bash"
        shcmd(cmdStr)    

    elif(args[0] == "stop"):
        cmdStr = "sudo docker stop " + "borregorun"
        shcmd(cmdStr)

    elif(args[0] == "rm"):
        cmdStr = "sudo docker container rm  " + "borregorun"
        shcmd(cmdStr)
    
    elif(arg == "quit"):
        exit()

    elif(arg == "q"):
        exit()         

    else:
       print(commands) 
