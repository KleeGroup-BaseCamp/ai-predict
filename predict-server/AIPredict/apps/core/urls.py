from django.urls import path
from AIPredict.apps.core import views
from django.http import HttpRequest


urlpatterns = [
    path('bundles/', views.BundlesView.as_view({'get': 'bundles'})),
    path('bundle/<str:bundle>/', views.BundlesView.as_view({'get': 'bundle'})),
    path('bundle/<str:bundle>/<int:version>/',
         views.BundlesView.as_view({'get': 'bundle'})),
    path('remove/<str:bundle>/',
         views.BundlesView.as_view({'delete': 'remove'})),
    path('remove/<str:bundle>/<int:version>/',
         views.BundlesView.as_view({'delete': 'remove'})),
    path('download/<str:bundle>/',
         views.BundlesView.as_view({'get': 'download'})),
    path('download/<str:bundle>/<int:version>/',
         views.BundlesView.as_view({'get': 'download'}))
]
