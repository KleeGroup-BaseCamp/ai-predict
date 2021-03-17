from django.db import models

class Bundle(models.Model):
    name = models.CharField(max_length=100)
    version = models.IntegerField(default='0')
    path = models.CharField(max_length=100)
    activated = models.BooleanField(default=False)

    class Meta:
        unique_together = ('name', 'version',)
    