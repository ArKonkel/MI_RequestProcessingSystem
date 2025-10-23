package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.role;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    /**
     * Retrieves a {@link Role} entity by its name. If no role is found with the given name,
     * a {@link NotFoundException} is thrown.
     *
     * @param name the name of the role to retrieve
     * @return the {@link Role} associated with the provided name
     * @throws NotFoundException if a role with the specified name is not found
     */
    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new NotFoundException("Role with name " + name + " not found"));
    }
}
