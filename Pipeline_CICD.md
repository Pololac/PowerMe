```mermaid
sequenceDiagram
  participant Dev
  participant GitHub Actions
  participant GHCR
  participant VPS

  Dev->>GitHub Actions: push/PR (main)
  GitHub Actions->>GitHub Actions: paths-filter (backend/frontend)
  alt backend modifié
    GitHub Actions->>GitHub Actions: mvn test && mvn package
  end
  alt frontend modifié
    GitHub Actions->>GitHub Actions: npm ci && test && build
  end

  Dev->>GitHub Actions: tag vX.Y.Z
  GitHub Actions->>GHCR: docker login + build/push backend
  GitHub Actions->>GHCR: docker login + build/push frontend
  GitHub Actions->>VPS: SSH (cd $DEPLOY_DIR)
  GitHub Actions->>VPS: docker compose pull
  GitHub Actions->>VPS: docker compose up -d
  VPS-->>Dev: nouvelle version en ligne

  ```

  ```mermaid
  flowchart LR
  A[Dev push/PR -> main] --> B{paths-filter}
  B -->|backend/**| C[Backend CI\nsetup-java + mvn test/package]
  B -->|frontend/**| D[Frontend CI\nsetup-node + npm ci/test/build]
  C --> E[Artifact JAR]
  D --> F[Artifact dist/]

  subgraph Release
    G[GHCR Login] --> H[Build&Push Image Backend]
    G --> I[Build&Push Image Frontend]
    H --> J[Images: backend:tag,latest]
    I --> K[Images: frontend:tag,latest]
    J --> L[SSH vers VPS]
    K --> L
    L --> M[docker compose pull]
    M --> N[docker compose up -d]
  end

  ```

