from bluetooth import*
import RPi.GPIO as GPIO
import os
import glob
import time

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(18, GPIO.OUT)

os.system('modprobe w1-gpio')
os.system('modprobe w1-therm')

base_dir = '/sys/bus/w1/devices/'
device_folder = glob.glob(base_dir + '28*')[0]
device_file = device_folder + '/w1_slave'

def read_temp_raw():
	f = open(device_file, 'r')
	lines = f.readlines()
	f.close()
	return lines

def read_temp():
	lines = read_temp_raw()
	while lines[0].strip()[-3:] != 'YES':
		time.sleep(0.2)
		lines = read_temp_raw()
	equals_pos = lines[1].find('t=')
	if equals_pos != -1:
		temp_string = lines[1][equals_pos+2:]
		temp_c = float(temp_string) / 1000.0
		temp_f = temp_c * 9.0 / 5.0 +32.0
		return temp_f

server_sock = BluetoothSocket(RFCOMM)
server_sock.bind(("", PORT_ANY))
server_sock.listen(1)

uuid = "00001101-0000-1000-8000-00805f9b34fb"

advertise_service(server_sock, "ChristinesPiServer",
                  service_id = uuid,
		  service_classes = [uuid, SERIAL_PORT_CLASS ],
                  profiles = [ SERIAL_PORT_PROFILE ],
		 )

counter = 0

while True:
    print "Waiting for connection on RFCOMM"
    client_sock, client_info = server_sock.accept()

    print "Accepted connection from: ", client_info
    try:
        data = client_sock.recv(1024)
        if len(data) == 0: break
        print "received [%s]" % data
        if data == 'On':
            GPIO.output(18, GPIO.HIGH)
            data = 'Light on!'
        elif data == 'Off':
            GPIO.output(18, GPIO.LOW)
            data = 'Light off!'
        elif data == 'Temp':
            data = str(read_temp()) + '!'
        else:
            data = "What?!"
        client_sock.send(data)
        print "sending [%s]" % data

    except IOError:
        pass

    except KeyboardInterrupt:
        print "disconnected"

        client_sock.close()
        server_sock.close()
        print "all done!"

        break
