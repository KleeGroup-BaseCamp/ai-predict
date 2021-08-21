<template>
  <q-card class="create-card">
    <q-card-section>
      <div class="text-subtitle1 text-primary">Metadata</div>
    </q-card-section>
    <q-separator color="accent" inset />
    <q-card-section class="column">
      <div class="row justify-center">
        <div class="column col-9 q-pa-sm">
          <q-input filled label="Name" v-model="meta.name" />
        </div>
        <div class="column col-3 q-pa-sm">
          <q-input
            filled
            label="Version"
            type="number"
            v-model.number="meta.version"
          />
        </div>
      </div>
      <br />
      <div class="row justify-center">
        <div class="column col-6 q-pa-sm">
          <q-select
            filled
            label="Language"
            :options="languageOption"
            v-model="meta.language"
          />
        </div>
        <div class="column col-6 q-pa-sm">
          <q-select
            filled
            label="Framework"
            :options="frameworkOption"
            v-model.number="meta.framework"
          />
        </div>
      </div>
      <br />
      <div class="row justify-center">
        <div class="column col-12 q-pa-sm">
          <q-input
            filled
            class="col-10"
            label="Datasource"
            v-model="meta.datasource"
          />
        </div>
      </div>
      <div class="row justify-center">
        <div class="column col-12 q-pa-sm">
          <q-input
            filled
            class="col-10"
            label="Description"
            type="textarea"
            v-model="meta.description"
          />
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: "Metadata",
  data() {
    return {
      meta: {
        name: "",
        version: 0,
        language: "",
        framework: "",
        datasource: ""
      },
      languageOption: ["python"],
      frameworkOption: ["scikit-learn", "custom", "keras", "xgboost"].sort()
    };
  }
};
</script>
