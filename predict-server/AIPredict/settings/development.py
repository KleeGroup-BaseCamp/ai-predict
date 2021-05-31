import os
import socket
from AIPredict.settings.production import *

TMP_PATH = os.path.abspath(os.path.join(BASE_DIR, 'tmp'))

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3' 

DEBUG = True
SECRET = '42'

INTERNAL_IPS = ('127.0.0.1',)
ALLOWED_HOSTS.append('127.0.0.1')

EMAIL_BACKEND = 'django.core.mail.backends.console.EmailBackend'

LOGGING = {
    'version': 1,
    'disable_existing_loggers': False,
    'formatters': {
        'verbose': {
            'format': '{levelname} {asctime} {module} {process:d} {thread:d} {message}',
            'style': '{',
        },
        'simple': {
            'format': '{levelname} {message}',
            'style': '{',
        },
    },
    'handlers': {
        'console': {
            'class': 'logging.StreamHandler',
            'formatter' : 'verbose'
        },
    },
    'loggers': {
        'django': {
            'handlers': ['console'],
            'level': 'INFO',
        },
    },
}

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