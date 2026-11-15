{{/*
Base name of all resources: the release name, or "<release>-catalog-service" when the release
name does not already contain the chart name.
*/}}
{{- define "catalog.fullname" -}}
{{- if contains .Chart.Name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "catalog.mongodb.fullname" -}}
{{- printf "%s-mongodb" (include "catalog.fullname" . | trunc 55 | trimSuffix "-") -}}
{{- end -}}

{{/* Labels that identify the pods; used in selectors, so they must never change. */}}
{{- define "catalog.selectorLabels" -}}
app.kubernetes.io/name: {{ .Chart.Name }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/component: api
{{- end -}}

{{- define "catalog.mongodb.selectorLabels" -}}
app.kubernetes.io/name: {{ .Chart.Name }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/component: mongodb
{{- end -}}

{{/* Informational labels put on every resource. */}}
{{- define "catalog.commonLabels" -}}
helm.sh/chart: {{ printf "%s-%s" .Chart.Name .Chart.Version }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: ticketflow
{{- end -}}

{{/* Connection string: the bundled MongoDB, or `mongodb.externalUri` when it is disabled. */}}
{{- define "catalog.mongodb.uri" -}}
{{- if .Values.mongodb.enabled -}}
{{- printf "mongodb://%s:27017/%s" (include "catalog.mongodb.fullname" .) .Values.mongodb.database -}}
{{- else -}}
{{- required "mongodb.externalUri is required when mongodb.enabled=false" .Values.mongodb.externalUri -}}
{{- end -}}
{{- end -}}
