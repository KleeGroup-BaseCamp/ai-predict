from django.urls import path
from AIPredict.apps.predict import views
from django.http import HttpRequest
import logging

logger = logging.getLogger(__name__)

# Wire up our API using automatic URL routing.
# Additionally, we include login URLs for the browsable API.
urlpatterns = [
    path('deploy/', views.DeployBundle.as_view({'post': 'create'})),
    path('activate/<str:bundle>/<int:version>/', views.DeployBundle.as_view({'put': 'activate'})),
    path('delete/<str:bundle>/<int:version>/', views.DeployBundle.as_view({'delete': 'destroy'})),
    path('model-bundles/', views.DeployBundle.as_view({'get': 'list'})),
    path('predict/<str:bundle>/<int:version>/', views.Prediction.as_view({'post': 'predict'}))
]