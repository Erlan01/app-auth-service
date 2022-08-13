package ai.ecma.auth.common;

import ai.ecma.lib.entity.Permission;
import ai.ecma.lib.entity.PermissionRole;
import ai.ecma.lib.entity.Role;
import ai.ecma.lib.entity.User;
import ai.ecma.lib.enums.PermissionEnum;
import ai.ecma.lib.enums.RoleTypeEnum;
import ai.ecma.lib.repository.PermissionRepository;
import ai.ecma.lib.repository.PermissionRoleRepository;
import ai.ecma.lib.repository.RoleRepository;
import ai.ecma.lib.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Osiyo Adilova
 * @project app-eticket-server
 * @since 12/16/2021
 */

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionRoleRepository permissionRoleRepository;

    @Value("${spring.sql.init.mode}")
    private String initialMode;

    @Override
    public void run(String... args) throws Exception {
        if (initialMode.equals("always")) {
            RoleTypeEnum[] values = RoleTypeEnum.values();

            Role sysAdminRole = null;

            PermissionEnum[] permissionEnums = PermissionEnum.values();
            List<Permission> permissionList = permissionRepository.saveAll(Arrays.stream(permissionEnums).map(Permission::new).collect(Collectors.toList()));

            List<Role> roles = new ArrayList<>();
            List<PermissionRole> permissionRoleList = new ArrayList<>();
            for (RoleTypeEnum roleTypeEnum : values) {
                if (roleTypeEnum != RoleTypeEnum.OTHER) {
                    Role role = new Role(roleTypeEnum.name(), roleTypeEnum);
                    roles.add(role);

                    List<Permission> collect;
                    if (roleTypeEnum == RoleTypeEnum.SYSTEM_ADMIN) {
                        collect = permissionList;
                        sysAdminRole = role;
                    } else {
                        collect = permissionList.stream().filter(p -> p.getName().getRoleType() == roleTypeEnum).collect(Collectors.toList());
                    }
                    permissionRoleList.addAll(collect.stream().map(p -> new PermissionRole(role, p)).collect(Collectors.toList()));
                }
            }

            roleRepository.saveAll(roles);
            permissionRoleRepository.saveAll(permissionRoleList);

            User adminUser = User.builder()
                    .setEmail("blabla@gmail.com")
                    .setFirstName("Admin")
                    .setPassword(passwordEncoder.encode("root123"))
                    .setPhoneNumber("+998901112233")
                    .setRole(sysAdminRole)
                    .setEnabled(true)
                    .setPermissions(new HashSet<>(permissionList))
                    .setAccountNonExpired(true)
                    .setCredentialsNonExpired(true)
                    .setAccountNonLocked(true)
                    .build();
            userRepository.save(adminUser);

        }
    }
}
