import os
import socket
from AIPredict.settings.production import *

TMP_PATH = os.path.abspath(os.path.join(BASE_DIR, 'tmp'))

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3' 

DEBUG = True
SECRET = '42'

INTERNAL_IPS = ('127.0.0.1',)
ALLOWED_HOSTS.append('127.0.0.1')
ALLOWED_HOSTS.append('localhost')

EMAIL_BACKEND = 'django.core.mail.backends.console.EmailBackend'



TRAIN_DB = {
    'traindb' : 
        {
            "sql" : "postgresql",
            "username" : "postgres",
            "password" : "admin",
            "host" : "localhost",
            "keyspace" : "traindb"
        },
    'cassandra_spark' : 
        {
            "username" : "cassandra",
            "password" : "cassandra",
            "keyspace" : "traindb",
            "cluster" : [socket.gethostbyname(socket.gethostname())]
        }
}