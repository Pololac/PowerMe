import { AfterViewInit, ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';
import { MapService } from '../services/map-service';
import maplibregl from 'maplibre-gl';
import { ChargingLocationStore } from '../charging-location-modal/services/charging-location.store';

@Component({
  selector: 'app-map-view',
  imports: [],
  templateUrl: './map-view.html',
  styleUrl: './map-view.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MapView implements AfterViewInit {
  private readonly mapService = inject(MapService);
  private readonly locationStore = inject(ChargingLocationStore);

  private map!: maplibregl.Map;
  private readonly markers = new Map<number, maplibregl.Marker>();
  private searchMarker?: maplibregl.Marker;

  constructor() {
    this.bindState();
  }

  ngAfterViewInit() {
    this.initMap();
  }

  // Crée une carte Maplibre et l'affiche dans l'élément #map
  private initMap() {
    this.map = new maplibregl.Map({
      container: 'map',
      center: [4.85, 45.75], // Coordonnées de Paris
      zoom: 10, // Niveau de zoom initial
      minZoom: 7, // Zoom minimum autorisé
      maxZoom: 18, // Zoom maximum autorisé
      maxPitch: 50, // Inclinaison maximale de la carte
      style: {
        version: 8,
        sources: {
          osm: {
            type: 'raster',
            tiles: [
              'https://api.maptiler.com/maps/streets-v2/256/{z}/{x}/{y}.png?key=xcQOHHgXyI5fUgSQrZ70',
            ],
            tileSize: 256,
          },
        },
        layers: [
          {
            id: 'osm',
            type: 'raster',
            source: 'osm',
          },
        ],
      },
    });

    // Ajout boutons de Zoom & Boussole
    this.map.addControl(
      new maplibregl.NavigationControl({
        visualizePitch: true,
        visualizeRoll: true,
        showCompass: false, // Désactive boussole
      }),
      'bottom-right',
    );

    // CENTRAGE PAGE SUR POSITION USER
    // Crée un contrôle de géolocalisation
    const geolocate = new maplibregl.GeolocateControl({
      positionOptions: { enableHighAccuracy: true },
      trackUserLocation: true,
    });

    // Ajout du bouton de géolocalisation à la carte
    this.map.addControl(geolocate, 'bottom-right');

    // Lance la géolocalisation de l'utilisateur au chargement de la page (évènement)
    this.map.on('load', () => {
      geolocate.trigger();
    });

    // Qd user localisé, lance ces actions
    geolocate.on('geolocate', (e) => {
      this.mapService.setUserPosition([e.coords.longitude, e.coords.latitude]);

      // Charger les stations après le centrage
      this.loadLocationsInView();
    });

    // Récupère les stations ds cadre qd on zoome
    this.map.on('moveend', () => {
      this.loadLocationsInView();
    });
  }

  // Observe l'état des signals définis dans MapService
  private bindState() {
    // Centre la carte
    effect(() => {
      const center = this.mapService.center();
      const zoom = this.mapService.zoom();

      if (!center) return;

      this.map.flyTo({
        center,
        zoom: zoom ?? this.map.getZoom(),
      });
    });

    // Ajoute les stations de recharge
    effect(() => {
      const locations = this.mapService.locations();

      this.markers.forEach((m) => m.remove());
      this.markers.clear();

      locations.forEach((loc) => {
        const marker = new maplibregl.Marker({ color: '#22c55e' })
          .setLngLat([loc.longitude, loc.latitude])
          .addTo(this.map);

        // Rend cliquable les stations
        const el = marker.getElement();
        el.style.cursor = 'pointer';

        // Ouvre la modale de la station
        el.addEventListener('click', (event) => {
          event.stopPropagation(); // évite les effets de bord (ex: click map / overlay)
          this.locationStore.loadLocationDetail(loc.id);
        });

        this.markers.set(loc.id, marker);
      });
    });

    // Ajoute un marqueur de recherche
    effect(() => {
      const coords = this.mapService.searchPosition();
      if (!coords) return;

      this.searchMarker?.remove();

      this.searchMarker = new maplibregl.Marker({ color: '#2563eb' })
        .setLngLat(coords)
        .addTo(this.map);
    });
  }

  // charge les stations situées dans le cadre affiché
  private loadLocationsInView() {
    const b = this.map.getBounds();

    this.mapService.loadLocationsInBounds({
      north: b.getNorth(),
      south: b.getSouth(),
      east: b.getEast(),
      west: b.getWest(),
    });
  }
}
