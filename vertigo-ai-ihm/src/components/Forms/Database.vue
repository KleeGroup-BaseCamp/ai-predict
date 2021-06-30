<template>
  <q-card class="create-card">
    <q-card-section>
      <div class="text-subtitle1 text-primary">Database Configuration</div>
    </q-card-section>
    <q-separator color="accent" inset />
    <q-card-section>
      <div class="row justify-center">
        <div class="column col-4 q-pa-sm">
          <q-input
            filled
            class="col-3"
            label="Database key"
            v-model="database.key"
          />
        </div>
        <div class="column col-4 q-pa-sm">
          <q-input
            filled
            class="col-3"
            label="Database name"
            v-model="database.database"
          />
        </div>
        <div class="column col-4 q-pa-sm">
          <q-input
            filled
            class="col-3"
            label="Table name"
            v-model="database.table"
          />
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: "Database",
  data() {
    return {
      database: {
        key: "",
        table: "",
        database: ""
      }
    };
  }
};
</script>
