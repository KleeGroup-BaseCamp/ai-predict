from django.urls import path
from AIPredict.apps.core import views
from django.http import HttpRequest


# Wire up our API using automatic URL routing.
# Additionally, we include login URLs for the browsable API.
urlpatterns = [
    path('bundles/', views.BundlesLister.as_view({'get': 'bundles'}))
]
