<template>
  <q-page padding class="row">
    <div class="row col-12">
      <h1 class="col text-h6 text-primary items-center">
        Bundle creation form
      </h1>
      <q-btn
        class="self-center"
        color="accent"
        icon="send"
        label="Send"
        style="height: 40px;"
        @click="sendBundle()"
      />
    </div>
    <div class="column col-7 q-gutter-md q-pa-sm">
      <Metadata ref="metaForm" />
      <Datatable ref="datatableForm" />
    </div>
    <div class="column col-5 q-gutter-md q-pa-sm">
      <Database ref="databaseForm" />
      <Algorithm ref="algorithmForm" />
      <Parameters ref="parametersForm" />
      <Hyperparameters ref="hyperparametersForm" />
    </div>
  </q-page>
</template>

<script>
import Metadata from "../components/Forms/Metadata";
import Algorithm from "../components/Forms/Algorithm";
import Database from "../components/Forms/Database";
import Datatable from "../components/Forms/Datatable";
import Parameters from "../components/Forms/Parameters";
import Hyperparameters from "../components/Forms/Hyperparameters";
import axios from "axios";
export default {
  components: {
    Metadata,
    Algorithm,
    Database,
    Datatable,
    Parameters,
    Hyperparameters
  },
  async mounted() {
    if (this.$route.params.bundle) {
      const url = "/api/bundle"
        .concat("/", this.$route.params.bundle)
        .concat("/", this.$route.params.version)
        .concat("", "/");
      let repsonse = await axios
        .get(url)
        .then(response => (this.forkData = response.data));
      this.$refs.metaForm.meta = this.forkData.meta;
      this.$refs.algorithmForm.algorithm = this.forkData.algorithm;
      this.$refs.databaseForm.database = this.forkData.dataset.db_config;
      this.$refs.datatableForm.datatable = this.forkData.dataset.data_config;
      this.$refs.parametersForm.parameters = this.forkData.parameters;
      this.$refs.hyperparametersForm.rawHyperparameters = JSON.stringify(
        this.forkData.parameters.hyperparameters
      );
      this.domains = Object.assign(this.domains, this.forkData.dataset.domains);
      this.$refs.metaForm.meta.status = "";
      this.$refs.metaForm.meta.version = this.$refs.metaForm.meta.version + 1 ;
    }
  },
  data() {
    return {
      forkData: {},
      domains: {
        String: "str",
        Float: "float",
        Integer: "int",
        Boolean: "bool"
      }
    };
  },
  methods: {
    async sendBundle() {
      var parameters = this.$refs.parametersForm.parameters;
      parameters.hyperparameters = this.$refs.hyperparametersForm.parameters.hyperparameters;
      const bundle = {
        meta: this.$refs.metaForm.meta,
        algorithm: this.$refs.algorithmForm.algorithm,
        dataset: {
          db_config: this.$refs.databaseForm.database,
          data_config: this.$refs.datatableForm.datatable,
          domains: this.domains
        },
        parameters: parameters
      };
      let response = await axios.post("/api/deploy-train/", bundle)
      .catch(error => {
        this.error = true;
        this.notifyError(error);
      })
      .then(response => {
        if (!this.error){
          this.creteSucceed(response);
        }
      });
    },
    notifyError(error) {
      this.$q.notify({
        message: "An error occured during the creation of "
          .concat("", this.$refs.metaForm.meta.name)
          .concat(" v", this.$refs.metaForm.meta.version)
          .concat(":\n", error.response.data.error),
        color: "negative"
      });
    },
    creteSucceed(response) {

      window.location.href = '/explore'.concat("/", this.$refs.metaForm.meta.name);
    }
  }
}
</script>
