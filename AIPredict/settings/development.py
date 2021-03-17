import os
from AIPredict.settings.production import *

TMP_PATH = os.path.abspath(os.path.join(BASE_DIR, 'tmp'))

DEBUG = True
SECRET = '42'

INTERNAL_IPS = ('127.0.0.1',)
ALLOWED_HOSTS.append('127.0.0.1')

EMAIL_BACKEND = 'django.core.mail.backends.console.EmailBackend'