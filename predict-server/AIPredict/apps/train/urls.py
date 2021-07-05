from django.urls import path
from AIPredict.apps.train import views
from django.http import HttpRequest


# Wire up our API using automatic URL routing.
# Additionally, we include login URLs for the browsable API.
urlpatterns = [
    path('deploy-train/', views.TrainModel.as_view({'post': 'deploy'})),
    path('train/<str:bundle>/<int:version>/',
         views.TrainModel.as_view({'post': 'retrain'})),
    path('score/<str:bundle>/<int:version>/',
         views.TrainModel.as_view({'post': 'score'})),
]
