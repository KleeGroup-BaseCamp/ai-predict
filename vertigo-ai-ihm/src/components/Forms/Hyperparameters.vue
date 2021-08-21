<template>
  <q-card class="create-card">
    <q-card-section class="row">
      <div class="col-11 text-subtitle1 text-primary">Hyperparameters</div>
      <div v-if="error" class="justify-center">
        <q-icon
          name="warning"
          color="negative"
          class="text"
          style="font-size: 1.5em; max-width: 5px;"
        />
        <q-tooltip>
          {{ jsonError }}
        </q-tooltip>
      </div>
    </q-card-section>
    <q-separator color="accent" inset />
    <q-card-section>
      <div class="row justify-center">
        <div class="column col-12 q-pa-sm">
          <q-input
            filled
            class="col-11"
            label="Hyperparameters"
            type="textarea"
            v-model="rawHyperparameters"
          />
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: "Hyperparameters",
  data() {
    return {
      jsonError: "",
      error: false,
      rawHyperparameters: "{\n\n}",
      hyperparameters: {},
      parameters: {
        hyperparameters: {}
      }
    };
  },
  watch: {
    rawHyperparameters: function(newValue) {
      try {
        this.parameters.hyperparameters = JSON.parse(newValue);
        this.error = false;
      } catch (e) {
        this.error = true;
        this.jsonError = e.message;
      }
    }
  }
};
</script>
