<template>
  <q-page padding>

    <section class="row q-gutter-lg q-pa-sm justify-center">
      <BundleSummary v-if="bundle" class="col-4" :bundle="bundle" />
      <BundleChart v-if="bundle" class="col-7" :vlist="versions" />
    </section>

    <section class="row q-gutter-lg q-pa-sm justify-center">
        <q-scroll-area
          class="q-pa-xs col-3 version-scroll"
          :key="versions.length"
        >
          <div
            v-for="version in versions.slice().reverse()"
            :key="version.version"
          >
            <VersionCard
              :version="version.version"
              :selectedVersion="selectedVersion"
              :activeVersion="activeVersion"
              :score="version.score"
              :bestVersion="bundle.bestVersion"
              @selected="handleSelect($event)"
            />
          </div>
        </q-scroll-area>
      <div class="q-pa-xs col-8">
        <ParametersCard
          v-if="versionData"
          :data="versionData"
          @newActive="handleNewActive($event)"
          @removedVersion="handleRemovedVersion($event)"
          @newVersion="handleRetrain($event)"
        />
      </div>
    </section>

  </q-page>
</template>

<script>
import BundleSummary from "../components/Cards/BundleSummary.vue";
import BundleChart from "../components/Cards/BundleChart.vue";
import axios from "axios";
import VersionCard from "src/components/Cards/VersionCard.vue";
import ParametersCard from "src/components/Cards/ParametersCard.vue";
export default {
  components: { BundleSummary, BundleChart, VersionCard, ParametersCard },
  name: "Overview",
  data() {
    return {
      versions: [],
      scores: [],
      reverseVersions: [],
      bundle: null,
      selectedVersion: -1,
      versionData: null,
      activeVersion: null
    };
  },
  mounted() {
    this.getData();
  },
  methods: {
    handleSelect(e) {
      this.selectedVersion = e;
      this.getVersionData();
    },
    getVersionData() {
      const url = "/bundle"
        .concat("/", this.$route.params.bundle)
        .concat("/", this.selectedVersion);
      axios.get(url).then(response => (this.versionData = response.data));
      return this.versionData;
    },
    handleNewActive(e) {
      this.activeVersion = e;
    },
    handleRemovedVersion(e) {
      this.versions = this.versions.filter(function(version) {
        return version.version != e;
      });
      if (this.selectedVersion == e) {
        this.selectedVersion = this.reverseVersions[0];
      }
      this.getData();
    },
    handleRetrain(e) {
      this.getData();
    },
    async getData() {
      const url = "/bundle".concat(
        "/",
        this.$route.params.bundle
      );
      await axios.get(url).then(response => {
        this.bundle = response.data.bundle;
        this.versions = response.data.versions.sort(function(a, b) {
          return a.version - b.version;
        });
        this.activeVersion = response.data.bundle.activeVersion;
      });
      this.selectedVersion = this.versions[this.versions.length - 1].version;
      this.getVersionData();
    }
  }
};
</script>
