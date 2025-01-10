ciao

# Profile Service

![CI status](https://github.com/letsstreamit/profile-service/actions/workflows/dispatcher.yml/badge.svg)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Version](https://img.shields.io/github/v/release/letsstreamit/profile-service?style=plastic)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=LetsStreamIt_profile-service&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=LetsStreamIt_profile-service)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=LetsStreamIt_profile-service&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=LetsStreamIt_profile-service)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=LetsStreamIt_profile-service&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=LetsStreamIt_profile-service)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=LetsStreamIt_profile-service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=LetsStreamIt_profile-service)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=LetsStreamIt_profile-service&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=LetsStreamIt_profile-service)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=LetsStreamIt_profile-service&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=LetsStreamIt_profile-service)

Profile service is responsible for managing the user profile information in the LetsStreamIt platform.
It manages the user's username, bio, and watched videos.

## Technologies

![Scala](https://img.shields.io/badge/scala-%23DC322F.svg?style=for-the-badge&logo=scala&logoColor=white)
![Akka](https://img.shields.io/badge/akka-%1396B9.svg?style=for-the-badge&logo=scala&logoColor=white)

### Infrastructure

[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://docker.com)

### DevOps

[![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)](https://github.com/features/actions)
[![Semantic Release](https://img.shields.io/badge/Semantic_Release-494949?style=for-the-badge&logo=semantic-release&logoColor=white)](https://semantic-release.gitbook.io/)
[![Semantic Versioning](https://img.shields.io/badge/Semantic_Versioning-333333?style=for-the-badge&logo=semver&logoColor=white)](https://semver.org/)
[![Conventional Commits](https://img.shields.io/badge/Conventional_Commits-FE5196?style=for-the-badge&logo=conventionalcommits&logoColor=white)](https://www.conventionalcommits.org/en/v1.0.0/)
[![Renovate](https://img.shields.io/badge/RenovateBot-1A1F6C?style=for-the-badge&logo=renovate&logoColor=white)](https://renovatebot.com/)
[![SonarCloud](https://img.shields.io/badge/SonarCloud-F3702A?style=for-the-badge&logo=sonarcloud&logoColor=white)](https://sonarcloud.io/)

## Getting Started

### Prerequisites

In order to run the project, you need to have the following tools installed on your machine:

- [sbt](https://www.scala-sbt.org/)
- [node.js](https://nodejs.org/) and [npm](https://www.npmjs.com/) (optional, only required for the git hooks)
- [git](https://git-scm.com/)

### Installation

To install the project, you need to clone the repository:

```bash
git clone https://github.com/LetsStreamIt/profile-service.git
cd profile-service
```

### Usage

1. You can either run the application using the `sbt` command:

   ```bash
   sbt run
   ```

2. Or you can build the Docker image:

   ```bash
   sbt docker:publishLocal
   ```

   And run it using Docker:

   ```bash
   docker run -p 8080:8080 ghcr.io/letsstreamit/profile-service:latest
   ```

3. Or using docker-compose in a similar way to the one found at the [LetsStreamIt/bootstrap](https://github.com/LetsStreamIt/bootstrap) repository.

### Configuration

If you want to configure the application, you may set the following environment variables:

| Variable                   | Description                                                                              | Default     |
| -------------------------- | ---------------------------------------------------------------------------------------- | ----------- |
| `PROFILE_SERVICE_HOSTNAME` | The hostname of the profile service                                                      | `localhost` |
| `PROFILE_SERVICE_PORT`     | The port of the profile service                                                          | `8080`      |
| `MONGO_HOSTNAME`           | The hostname of the MongoDB server                                                       | `localhost` |
| `MONGO_PORT`               | The port of the MongoDB server                                                           | `27017`     |
| `AUTH_SERVICE_HOSTNAME`    | The hostname of the auth service                                                         | `localhost` |
| `AUTH_SERVICE_PORT`        | The port of the auth service                                                             | `3000`      |
| `AKKA_LICENSE_KEY`         | The license key for Akka, you can get it at [https://akka.io/key](https://akka.io/key) | `""`        |

## Documentation

The documentation and API reference can be found at the [API documentation](https://letsstreamit.github.io/profile-service).

## Contributing

If you want to contribute to the project, please read the [CONTRIBUTING.md](CONTRIBUTING.md) file.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Authors

- Luca Fabri ([w-disaster](https://github.com/w-disaster))
- Simone Ceredi ([sceredi](https://github.com/sceredi))
