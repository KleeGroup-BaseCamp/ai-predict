from django.apps import AppConfig
from pathlib import Path

# import the logging library
import logging

# Get an instance of a logger
logger = logging.getLogger(__name__)

class AIPredictConfig(AppConfig):
    name = 'AIPredict'
    def ready(self):
        logger.info('Predict app Ready')
        # Ensure necessary directories exists
        path = "./bundles/temp/"
        Path(path).mkdir(parents=True, exist_ok=True)

