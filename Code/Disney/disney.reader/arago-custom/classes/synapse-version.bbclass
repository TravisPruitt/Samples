do_compile_append () {
	${OEBASE}/tools/version/version.sh ${WORKDIR} ${SYNAPSE_APPLICATIONS}
}

do_install_append () {
	rm      -rf	${D}/VERSION
	install -d	${D}/VERSION
	install -m 0755 ${WORKDIR}/VERSION/* ${D}/VERSION
}

pkg_preinst_append () {
    rm -rf /VERSION/
}


FILES_${PN}_append = "/VERSION/*"
