from django.apps import AppConfig
import logging

# Get an instance of a logger
logger = logging.getLogger(__name__)

class CoreConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'AIPredict.apps.core'

    def ready(self):
        logger.info('Core App Ready')
