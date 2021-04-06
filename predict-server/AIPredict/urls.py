from django.urls import include, path
from rest_framework import routers
from AIPredict.apps.predict import views
from django.contrib import admin
from django.http import HttpRequest

# Wire up our API using automatic URL routing.
# Additionally, we include login URLs for the browsable API.
urlpatterns = [
    path('admin/', admin.site.urls),
    path('deploy/', views.DeployBundle.as_view({'post': 'create'})),
    path('auto-deploy/', views.DeployBundle.as_view({'post': 'auto_deploy'})),
    path('activate/<str:bundle>/<int:version>/', views.DeployBundle.as_view({'put': 'activate'})),
    path('delete/<str:bundle>/<int:version>/', views.DeployBundle.as_view({'delete': 'destroy'})),
    path('model-bundles/', views.DeployBundle.as_view({'get': 'list'})),
    path('predict/<str:bundle>/<int:version>/', views.Prediction.as_view()),
    path('api-auth/', include('rest_framework.urls', namespace='rest_framework'))
]

django_request = HttpRequest()
django_request.method = 'POST'
auto_deploy_view = views.DeployBundle.as_view({'post': 'auto_deploy'})
deploy = auto_deploy_view(request=django_request)

