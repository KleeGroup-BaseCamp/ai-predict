from django.apps import AppConfig
import logging

# Get an instance of a logger
logger = logging.getLogger(__name__)

try:
    import keras
    logger.info('Keras imported')
except:
    logger.info('Keras not imported')

try:
    import sklearn
    logger.info('Scikit-learn imported')
except:
    logger.info('Scikit-learn not imported')

try:
    import xgboost
    logger.info('XGBoost imported')
except:
    logger.info('XGBoost not imported')
 

class CoreConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'AIPredict.apps.core'

    def ready(self):
        logger.info('Core App Ready')
