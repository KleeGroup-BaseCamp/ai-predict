from django.contrib import admin

from django.contrib import admin
from AIPredict.apps.predict.models import Bundle

class BundleAdmin(admin.ModelAdmin):
    pass
admin.site.register(Bundle, BundleAdmin)
