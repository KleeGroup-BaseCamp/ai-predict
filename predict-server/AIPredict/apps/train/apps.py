from django.apps import AppConfig
# import the logging library
import logging

# Get an instance of a logger
logger = logging.getLogger(__name__)

class TrainConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'AIPredict.apps.train'

    def ready(self):
        logger.info('Train app Ready')
