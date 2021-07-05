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
      <div class="row text-primary text-center">
        <section class="col-4 column">
          <div class="col-auto text-subtitle1">Parameters</div>
          <div v-for="(item, key, index) in parametersSelector()" class="col column text-body2" :key="index">
            <div class="row q-gutter-lg ">
              <p class="col row self-start">{{key}}</p>
              <p class="col row justify-center text-accent">{{item}}</p>
            </div>
          </div>
        </section>
        <section class="col-4 column">
          <div class="col-auto text-subtitle1">Feature Importance</div>
          <FeatureImportancesChart
          class="col text-body2"
          :explanation="data.explanation"
        />
        </section>
        <section class="col-4 column">
          <div class="col-auto text-subtitle1">Training Data</div>
          <div class="col column text-body2 justify-center">
            <div class="row self-center q-gutter-lg">
              <p class="col-auto">Data shape</p>
              <div class="col-auto row">
                <p>{{ data.training_data.x_shape[0] }}</p>
                <p>x</p>
                <p>{{ data.training_data.x_shape[1] +  data.training_data.y_shape[1]}}</p>
              </div>
            </div>
            <div class="row self-center q-gutter-lg">
              <p class="col-auto">Training time</p>
              <div class="col-auto row">
                <p>{{ data.training_data.time.toPrecision(3) }}</p>
                <p>s</p>
              </div>
            </div>
          </div>
        </section>
      </div>
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
      const url = "/activate"
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
      const url = "/remove"
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
      const url = "/download"
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
      const url = "/train"
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
      if (this.data.training_parameters.estimator){
        const estimator = this.data.training_parameters.estimator
        return Object.fromEntries(Object.entries(estimator).filter(([_, v]) => (v != null && typeof v != "object")).slice(0, 5));
      }
    }
  }
};
</script>
