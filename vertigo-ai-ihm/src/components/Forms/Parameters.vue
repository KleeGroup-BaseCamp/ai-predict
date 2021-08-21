<template>
  <q-card class="create-card">
    <q-card-section>
      <div class="text-subtitle1 text-primary">Parameters</div>
    </q-card-section>
    <q-separator color="accent" inset />
    <q-card-section>
      <div class="row justify-center">
        <div class="column col-2 q-pa-sm">
          <q-input
            filled
            label="Jobs number"
            type="number"
            v-model.number="parameters.n_jobs"
          />
        </div>
        <div class="column col-2 q-pa-sm">
          <q-input
            filled
            class="col-2"
            label="CV"
            type="number"
            v-model.number="parameters.cv"
          />
        </div>
        <div class="column col-5 q-pa-sm">
          <q-input
            filled
            class="col-3"
            label="Metrics"
            v-model="parameters.metrics"
          />
        </div>
        <div class="column col-3 q-pa-sm justify-center text-dark">
          <q-checkbox
            filled
            class="col-3"
            label="Use grid search"
            v-model="parameters.grid_search"
          />
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: "Parameters",
  data() {
    return {
      parameters: {
        n_jobs: 1,
        cv: 5,
        metrics: "",
        grid_search: false,
        hyperparameters: "{\n\n}"
      }
    };
  }
};
</script>
