A Simple Chat to comunicate using your terminal

---------------------------------------------------

This project is a multi-client chat server implemented in Java. It allows multiple clients to connect to a server and engage in a chatroom where they can send messages to all connected users, send private messages, and manage blocked users.

____________________________________________________

How to Run the Project

Clone the Repository:
- git clone https://github.com/your-username/chat-server.git

Enter the build directory
- using the terminal enter directory build inside the project

Open the jar
- java -jar Simple-Chat.jar

This will iniciate the server 

After and using another terminal window you can enter the chat as a user using netcat

- Windowns - ncat localhost 9000
- MAC - nc localhost 9000


List of Commands
- /help: Displays a list of available commands.
- /users: Lists all online users.
- /whisper: Initiates a private chat with a specified user.
- /block: Blocks a specified user.
- /unblock: Unblocks a specified user.
- /bye: Disconnects from the server.

______________________________________________________________________________________

Thank you for checking out this project! Enjoy chatting!
