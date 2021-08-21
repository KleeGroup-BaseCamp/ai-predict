<template>
  <q-card class="create-card">
    <q-card-section class="row" style="max-height: 60px;">
      <div class="col text-subtitle1 text-primary">
        Datatable
      </div>
      <q-btn flat color="primary" @click="createFeature">
        <q-icon
          name="add"
          class="text"
          style="font-size: 1.5em; max-width: 5px;"
        />
        <q-tooltip>
          New Feature
        </q-tooltip>
      </q-btn>
    </q-card-section>
    <q-separator color="accent" inset />
    <q-card-section>
      <div v-for="(feature, index) in datatable" :key="feature.id">
        <div class="row justify-center">
          <div class="column col-3 q-pa-sm justify-center">
            <q-input filled label="Name" v-model="feature.name" />
          </div>
          <div class="column col-2 q-pa-sm justify-center">
            <q-select
              filled
              label="Type"
              v-model="feature.domain"
              :options="typeOption"
            />
          </div>
          <div class="column col-2 q-pa-sm justify-center">
            <q-input filled label="Impute" v-model="feature.ifna" />
          </div>
          <div class="column col-3 q-pa-sm justify-center">
            <q-input
              filled
              label="Preprocessor"
              v-model="feature.preprocessing"
            />
          </div>
          <div class="column col-1 q-pa-sm justify-center">
            <div class="row justify-center text-dark">Label</div>
            <q-checkbox
              class="row justify-center"
              filled
              v-model="feature.is_label"
            />
          </div>
          <div class="column col-1 q-pa-sm justify-center">
            <q-btn flat color="primary button" @click="deleteFeature(index)">
              <q-icon
                name="remove"
                class="text"
              />
              <q-tooltip>
                Remove Feature
              </q-tooltip>
            </q-btn>
          </div>
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: "Datatable",
  data() {
    return {
      datatable: [],
      typeOption: ["String", "Boolean", "Integer", "Float"]
    };
  },
  mounted() {
    this.createFeature();
    this.createFeature();
    this.createFeature();
  },
  methods: {
    createFeature() {
      var feature = {
        id: this.datatable.length,
        name: "",
        domain: "",
        is_label: false,
        ifna: null,
        preprocessing: ""
      };
      this.datatable.push(feature);
    },
    deleteFeature(index) {
      this.datatable.splice(index, 1);
    },
    setIfNa(feature){
      const ifna = feature.ifna;
      const domain = feature.domain;
      if (domain != "" && ifna) {
        if (domain=="Integer") {
          feature.ifna = parseInt(ifna);
        } else if (domain=="Float"){
          feature.ifna = parseFloat(ifna);
        } else if (ifna=="True"){
          feature.ifna = true;
        } else if (ifna=="False"){
          feature.ifna = false;
        } else {
          feature.ifna = ifna;
        }
      }
    }
  }
};
</script>
