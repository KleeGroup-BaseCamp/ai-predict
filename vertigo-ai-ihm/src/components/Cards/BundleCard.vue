<template>
  <q-card
    bordered
    class="bg-white-9 q-pa-md "
    @mouseover="hover = true"
    @mouseleave="hover = false"
  >
    <q-card-section class="row">
      <div class="text-h6 text-primary col-8">{{ title }}</div>
      <div class="col-4 justify-end">
        <q-btn flat color="primary" :to="exploreURL">
          <q-icon
            v-if="hover"
            name="login"
            class="text"
            style="font-size: 1.5em; max-width: 5px;"
          />
          <q-tooltip>
            Explore
          </q-tooltip>
        </q-btn>
        <q-btn flat color="primary" @click="downloadBundle">
          <q-icon
            v-if="hover"
            name="o_file_download"
            class="text"
            style="font-size: 1.5em; max-width: 5px;"
          />
          <q-tooltip>
            Download
          </q-tooltip>
        </q-btn>
        <q-btn flat color="primary" @click="deleteBundle">
          <q-icon
            v-if="hover"
            name="clear"
            class="text"
            style="font-size: 1.5em; max-width: 5px;"
          />
          <q-tooltip>
            Delete
          </q-tooltip>
        </q-btn>
      </div>
    </q-card-section>

    <q-separator color="accent" inset />

    <q-card-section class="row bundle-items">
      <section class="col column items-center">
        <div class="text-h6 text-primary">Performance</div>
        <div>
          <performance-chart v-bind:value="performance"></performance-chart>
        </div>
      </section>
      <section class="col column items-center">
        <div class="text-h6 text-primary">Active</div>
        <div v-if="active" class="">
          <q-icon
            name="check_circle"
            class="text-positive"
            style="font-size: 3em;"
          />
        </div>
        <div v-else>
          <q-icon name="cancel" class="text-negative" style="font-size: 3em;" />
        </div>
      </section>
      <section class="col column items-center">
        <div class="text-h6 text-primary">Use</div>
        <div>
          <div class="text-h6 text-dark">{{ use }}</div>
        </div>
      </section>
    </q-card-section>
  </q-card>
</template>

<script>
import PerformanceChart from "../Charts/PerformanceChart";
import axios from "axios";

export default {
  name: "BundleCard",
  components: { PerformanceChart },
  props: {
    title: {
      type: String,
      required: true
    },

    performance: {
      type: Number,
      required: true
    },

    active: {
      type: Boolean,
      required: true
    },

    use: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      hover: false,
      deleteError: false,
      downloadURL: "/api/download".concat("/", this.title).concat("/", ""),
      exploreURL: "/explore".concat("/", this.title)
    };
  },
  methods: {
    deleteBundle() {
      const url = "/api/remove".concat("/", this.title);
      axios
        .delete(url)
        .catch(error => {
          this.deleteError = true;
          this.deleteAllNotifyError(error);
        })
        .finally(this.sendDeleteUpdate());
    },
    sendDeleteUpdate() {
      if (this.deleteError) {
        this.$emit("deletedBundle", this.title);
      }
      this.deleteError = false;
    },
    deleteAllNotifyError(error) {
      this.$q.notify({
        message: "An error occured during the deletion of "
          .concat("", this.title)
          .concat(":\n", error.message),
        color: "negative"
      });
    },
    downloadBundle() {
      const url = this.downloadURL;
      console.log(url);
      axios({
        method: "get",
        url: url,
        responseType: "blob"
      })
        .then(response => {
          const link = document.createElement("a");
          link.href = "/api/download/".concat("", this.title).concat("/", "");
          document.body.appendChild(link);
          link.click();
          console.log(link.href)
        })
        .catch(error => {
          this.downloadNotifyError(error);
        });
    },
    downloadNotifyError(error) {
      this.$q.notify({
        message: "An error occured during the download of "
          .concat("", this.title)
          .concat(":\n", error.message),
        color: "negative"
      });
    }
  }
};
</script>
