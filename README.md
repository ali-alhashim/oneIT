# OneIT Web Application User Manual

![OneIT Logo](/Users/ali/Documents/MyJava/oneIT/oneIT/src/main/resources/static/img/dark.jpg)

| Document Details |             |
|-----------------|-------------|
| Version         | 1.0         |
| Developer       | Ali Alhashim |
| Location        | Saudi Arabia |
| Year           | 2024-2025    |

## Table of Contents
1. [Introduction](#introduction)
2. [Security Features](#security-features)
3. [Database Configuration](#database-configuration)
4. [Modules Overview](#modules-overview)
5. [User Roles and Permissions](#user-roles-and-permissions)
6. [Getting Started](#getting-started)
7. [FAQs](#faqs)
8. [Support Contact Information](#support-contact-information)

## Introduction

https://youtu.be/rCu12fI0_90?si=NDAzl4l7m-5OafEy


OneIT is a comprehensive web application designed to streamline organizational workflows and eliminate reliance on paper-based systems.

### Core Technologies
- **Backend**: Java with Spring Framework
- **Frontend**: Thymeleaf, Bootstrap 5, and Select2
- **Mapping**: LeafletJS for geographical visualization

### Key Benefits
- ✓ Fast and secure processing
- ✓ Open-source with customization options
- ✓ Comprehensive logging and role-based access control

## Security Features

Built on the enterprise-grade Spring Framework, OneIT implements robust security measures trusted by financial institutions.

### Authentication & Access Control
- **Multi-Factor Authentication (MFA)**
  - TOTP integration with Google Authenticator
  - Enhanced security protocols
- **Role-Based Access Control**
  - Granular permission management
  - Secure user separation

### System Roles
| Role | Description |
|------|-------------|
| ADMIN | Full system permissions |
| HR | Manages employee records and HR functionalities |
| SUPPORT | Handles IT support tickets and related tasks |
| MANAGER | Manages department employees and their requests |
| PROCUREMENT | Oversees procurement processes |
| MEDICAL | Handles medical clearance processes |
| VEHICLE | Manages vehicle-related clearances |
| FINANCE | Processes financial clearances |

## Database Configuration

OneIT utilizes MySQL for data management. Follow these steps for setup:

1. Clone the repository:
   ```bash
   git clone https://github.com/ali-alhashim/oneIT/tree/master
   ```

2. Update database credentials in `config.properties`

3. Launch the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Modules Overview

### IT Services
- Device inventory management
- Asset tracking
- IT request processing
- Support ticket system

### Procurement Management
- Purchase order processing
- Invoice management
- Vendor database
- Procurement analytics

### HR Management
- Employee records
- Geolocation tracking
- Shift scheduling
- Timesheet processing

### System Administration
- System logs
- Backup management
- Role configuration
- Security settings

## User Roles and Permissions

Detailed role permissions are managed through a comprehensive matrix system. Contact your system administrator for specific role assignments.

## Getting Started

### First-Time Login
1. Use the default admin credentials provided
2. Complete MFA setup using Google Authenticator
3. Change your password immediately

### Accessing Modules
- Navigate through the intuitive dashboard
- Select your required module from the main menu
- Use the quick access sidebar for frequent tasks

### Password Management
- Administrators can reset user passwords
- Users must change temporary passwords at first login
- Password policies enforce strong security standards

## FAQs

**Q: Can I customize the app to suit my organization's needs?**  
A: Yes, OneIT is open-source and highly customizable. You can modify both frontend and backend components.

**Q: How are uploaded files managed?**  
A: Files are stored in a public folder with appropriate access controls and backup procedures.

## Support Contact Information

Need help? Contact our support team:

- **Developer**: Ali Alhashim
- **Email**: ali-alhashim@outlook.com
- **GitHub**: [OneIT Repository](https://github.com/ali-alhashim/oneIT/tree/master)

---

*Copyright © 2024-2025 OneIT. All rights reserved.*
