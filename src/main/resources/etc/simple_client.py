import threading
import paho.mqtt.client as mqtt
import time
'''
0: Connection successful
1: Connection refused - incorrect protocol version
2: Connection refused - invalid client identifier
3: Connection refused - server unavailable
4: Connection refused - bad username or password
5: Connection refused - not authorised
6-255: Currently unused.
'''
class SDK(threading.Thread):

    def __init__(self,host,port,open_id,group,user_id,on_message):
        super(SDK, self).__init__()
        self.open_id=open_id
        self.group=group
        self.user_id=user_id
        self.host=host
        self.port=port
        self.client = mqtt.Client(self.open_id)
        self.client.username_pw_set(open_id,open_id)
        self.client.on_message=on_message
        self.client.on_connect=self.on_connect
        self.client.on_disconnect=self.on_disconnect



    def run(self):
        self.client.connect(self.host, self.port, 60)
        self.client.loop_forever()

    def publish(self,data):
        self.client.publish("IN/DEVICE/"+CLIENT_USER_ID+"/"+CLIENT_GROUP+"/"+CLIENT_OPENID, str(data))

    def on_disconnect(self,a,b,c):
        print("已断开连接,状态码:",c)

    def on_connect(self,c, userdata, flags, rc):
            if rc==0:
                self.client.subscribe("OUT/DEVICE/"+self.user_id+"/"+self.group+"/"+self.open_id)
                print("连接成功!")
            elif rc==1:
                print("连接失败!MQTT协议错误!")
                self.client.disconnect()
                exit(1)
            elif rc==2:
                print("连接失败!非法客户端标识!")
                self.client.disconnect()
                exit(1)
            elif rc==3:
                print("连接失败!服务器访问失败!")
                self.client.disconnect()
            elif rc==4:
                print("连接失败!账户或者密码错误!")
                self.client.disconnect()
                exit(1)
            elif rc==5:
                print("连接失败!认证失败!")
                self.client.disconnect()
                exit(1)
            else :
                self.client.disconnect()
                exit(1)
