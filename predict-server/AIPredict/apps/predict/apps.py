from django.apps import AppConfig

from pathlib import Path
import logging

# Get an instance of a logger
logger = logging.getLogger(__name__)
class PredictConfig(AppConfig):
    name = 'AIPredict.apps.predict'
    verbose_name = "predict"
    default_auto_field = 'django.db.models.BigAutoField'
    def ready(self):
        logger.info('Predict app Ready')
        # Ensure necessary directories exists
        path = "./bundles/temp/"
        Path(path).mkdir(parents=True, exist_ok=True)

