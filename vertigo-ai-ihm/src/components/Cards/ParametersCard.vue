<template>
  <q-card>
    <q-card-section class="row">
      <div class="text-h6 text-primary col">
        {{ data.algorithm.package }}.{{ data.algorithm.name }}
      </div>
      <q-btn flat color="primary">
        <q-icon
          name="update"
          class="text"
          style="font-size: 1.5em; max-width: 5px;"
          @click="trainVersion()"
        />
        <q-tooltip>
          Retrain
        </q-tooltip>
      </q-btn>
      <q-btn
        v-if="data.algorithm.status != 'active'"
        flat
        color="primary"
        @click="activateBundle()"
      >
        <q-icon
          name="power_settings_new"
          class="text"
          style="font-size: 1.5em; max-width: 5px;"
        />
        <q-tooltip>
          Activate
        </q-tooltip>
      </q-btn>
      <q-btn flat color="primary">
        <q-icon
          name="o_file_download"
          class="text"
          style="font-size: 1.5em; max-width: 5px;"
          @click="downloadVersion()"
        />
        <q-tooltip>
          Download
        </q-tooltip>
      </q-btn>
      <q-btn
        flat
        color="primary"
        :to="
          '../fork'
            .concat('/', this.data.meta.name)
            .concat('/', this.data.meta.version)
        "
      >
        <q-icon
          name="alt_route"
          class="text"
          style="font-size: 1.5em; max-width: 5px;"
        />
        <q-tooltip>
          Fork
        </q-tooltip>
      </q-btn>
      <q-btn flat color="primary" @click="deleteVersion()">
        <q-icon
          name="clear"
          class="text"
          style="font-size: 1.5em; max-width: 5px;"
        />
        <q-tooltip>
          Delete
        </q-tooltip>
      </q-btn>
    </q-card-section>
    <q-separator color="accent" inset />
    <q-card-section>
      <section class="row text-subtitle1 text-center">
          <div class="col-4">Parameters</div>
          <div class="col-4">Feature Importance</div>
          <div class="col-4">Training Data</div>
      </section>
      <section class="row" style="min-height: 200px;">
        <div class="col-4 text-body2 column justify-center">
         <div v-for="(item, key, index) in data.major_parameters" :key="index">
            <div class="row" style="max-height: 36px;">
              <p class="offset-2 col">{{key}}</p>
              <p class="col text-accent">{{item}}</p>
            </div>
         </div>
        </div>
        <FeatureImportancesChart
        class="col-4 text-body2"
        :explanation="data.explanation"
      />
        <div class="col-4 text-body2 column justify-center" >
          <div class="row" style="max-height: 36px;"> 
            <p class="offset-3 col">Data shape</p>
            <div class="col text-accent">
              <p>{{ data.training_data.x_shape[0] }}x{{ data.training_data.x_shape[1] +  data.training_data.y_shape[1]}}</p>
            </div>
          </div>
          <div class="row" style="max-height: 36px;"> 
            <p class="offset-3 col">Training time</p>
            <div class="col text-accent ">
              <p>{{ data.training_data.time.toPrecision(3) }}s</p>
            </div>
          </div>
        </div>
      </section>
    </q-card-section>
  </q-card>
</template>

<script>
import axios from "axios";
import FeatureImportancesChart from "../Charts/FeatureImportancesChart.vue";
export default {
  name: "VersionsCard",
  components: { FeatureImportancesChart },
  data() {
    return { activationError: false, trainError: false, trainResponse: {} };
  },
  props: {
    data: {
      type: Object,
      required: true
    }
  },
  methods: {
    activateBundle() {
      const url = "/api/activate"
        .concat("/", this.data.meta.name)
        .concat("/", this.data.meta.version)
        .concat("", "/");
      axios
        .put(url)
        .catch(error => {
          this.activationError = true;
          this.notifyActivationError(error);
        })
        .finally(this.notifyActivation());
    },
    notifyActivation() {
      this.$emit("newActive", this.data.meta.version);
      if (!this.activationError) {
        this.$q.notify({
          message: "The bundle"
            .concat(" ", this.data.meta.name)
            .concat(" v", this.data.meta.version)
            .concat(" ", "is now active."),
          color: "positive"
        });
      }
    },
    notifyActivationError(error) {
      this.$q.notify({
        message: "The bundle"
          .concat(" ", this.data.meta.name)
          .concat(" v", this.data.meta.version)
          .concat(" ", "activation failed")
          .concat(": ", error.message),
        color: "negative"
      });
      this.activationError = false;
    },
    deleteVersion() {
      const url = "/api/remove"
        .concat("/", this.data.meta.name)
        .concat("/", this.data.meta.version)
        .concat("", "/");
      axios
        .delete(url)
        .catch(error => {
          this.activationError = true;
          this.notifyActivationError(error);
        })
        .finally(this.notifyVersionRemoving());
    },
    notifyVersionRemoving() {
      this.$emit("removedVersion", this.data.meta.version);
      if (!this.activationError) {
        this.$q.notify({
          message: "The bundle"
            .concat(" ", this.data.meta.name)
            .concat(" v", this.data.meta.version)
            .concat(" ", "is now removed."),
          color: "positive"
        });
      }
    },
    notifyVersionRemovingError(error) {
      this.$q.notify({
        message: "The bundle"
          .concat(" ", this.data.meta.name)
          .concat(" v", this.data.meta.version)
          .concat(" ", "removing failed")
          .concat(": ", error.message),
        color: "negative"
      });
    },
    downloadVersion() {
      const url = "/api/download"
        .concat("/", this.data.meta.name)
        .concat("/", this.data.meta.version)
        .concat("", "/");
      axios({
        method: "get",
        url: url,
        responseType: "blob"
      })
        .then(response => {
          const link = document.createElement("a");
          link.href = url;
          document.body.appendChild(link);
          link.click();
        })
        .catch(error => {
          this.downloadVersionNotifyError(error);
        });
    },
    downloadVersionNotifyError(error) {
      this.$q.notify({
        message: "An error occured during the download of "
          .concat("", this.title)
          .concat(":\n", error.message),
        color: "negative"
      });
    },
    async trainVersion() {
      const url = "/api/train"
        .concat("/", this.data.meta.name)
        .concat("/", this.data.meta.version)
        .concat("", "/");
      let response = await axios
        .post(url)
        .then(response => {
          this.trainResponse = response.data;
          console.log(response.data);
        })
        .catch(error => {
          this.trainError = true;
          this.notifyTrainError(error);
        });
      this.notifyTrain();
    },
    notifyTrain() {
      if (!this.trainError) {
        this.$emit("newVersion", true);
        this.$q.notify({
          message: this.trainResponse.modelName
            .concat(" v", this.trainResponse.version)
            .concat(" ", "trained in")
            .concat(" ", Math.round(this.trainResponse.time))
            .concat("s ", "with a score of")
            .concat(" ", this.trainResponse.score.scoreMean),
          color: "positive"
        });
      }
    },
    notifyTrainError(error) {
      this.$q.notify({
        message: "Error".concat(" : ", error.response.data.error),
        color: "negative"
      });
      this.activationError = false;
    },
    parametersSelector(){
      if (this.data.major_parameters){
        console.log(this.data.major_parameters)
        return this.data.major_parameters;
      }
    }
  }
};
</script>
