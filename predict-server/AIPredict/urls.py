from django.urls import include, path
from django.contrib import admin
from django.http import HttpRequest

# Wire up our API using automatic URL routing.
# Additionally, we include login URLs for the browsable API.
urlpatterns = [
    path('admin/', admin.site.urls),
    path('api-auth/', include('rest_framework.urls', namespace='rest_framework')),
    path('', include('AIPredict.apps.predict.urls')),
    path('', include('AIPredict.apps.train.urls')),
]
