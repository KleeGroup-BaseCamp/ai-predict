from django.db import models

class Bundle(models.Model):
    id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=100)
    version = models.IntegerField(default='0')
    activated = models.BooleanField(default=False)
    auto = models.BooleanField(default=False)

    class Meta:
        unique_together = ('name', 'version',)
    
    def __str__(self):
        res = self.name + " v" + str(self.version)
        return res
    